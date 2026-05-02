"""
Web Scraper - Extracts public routes (URLs) and textual content from a website,
including text inside linked PDF files, then saves the results to a CSV file.

Usage:
    python web_scraper.py --url https://example.com [options]

Options:
    --url           Base URL to start scraping (required)
    --output        Output CSV filename (default: scraped_data.csv)
    --max-pages     Maximum number of pages to scrape (default: 50)
    --delay         Delay in seconds between requests (default: 1.0)
    --depth         Maximum crawl depth from the base URL (default: 3)
    --allow-external    Follow links to other domains (default: False)
    --skip-pdfs     Do NOT extract text from PDF files (default: PDFs ARE scraped)

Requirements:
    pip install requests beautifulsoup4 lxml pdfplumber
"""

import argparse
import csv
import io
import time
import logging
from collections import deque
from datetime import datetime
from urllib.parse import urljoin, urlparse

import pdfplumber
import requests
from bs4 import BeautifulSoup

# ── Logging setup ──────────────────────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%H:%M:%S",
)
log = logging.getLogger(__name__)

# ── Constants ──────────────────────────────────────────────────────────────────
DEFAULT_HEADERS = {
    "User-Agent": (
        "Mozilla/5.0 (compatible; PythonWebScraper/1.0; "
        "+https://github.com/your-repo)"
    )
}

SKIP_EXTENSIONS = {
    ".jpg", ".jpeg", ".png", ".gif", ".svg", ".webp",
    ".mp4", ".mp3", ".zip", ".tar", ".gz", ".exe", ".dmg",
    ".css", ".js", ".woff", ".woff2", ".ttf", ".ico",
}

# Handled separately — scraped when --skip-pdfs is NOT set
PDF_EXTENSIONS = {".pdf"}


# ── Helpers ────────────────────────────────────────────────────────────────────

def normalise_url(url: str) -> str:
    """Strip fragments so #section links don't create duplicates."""
    parsed = urlparse(url)
    return parsed._replace(fragment="").geturl()


def is_scrapable(url: str, base_domain: str, same_domain: bool,
                 skip_pdfs: bool = False) -> bool:
    """Return True if the URL should be queued for scraping."""
    parsed = urlparse(url)

    # Must be http/https
    if parsed.scheme not in ("http", "https"):
        return False

    # Optionally restrict to the same domain
    if same_domain and parsed.netloc != base_domain:
        return False

    path_lower = parsed.path.lower()

    # Skip hard-blocked binary/static assets
    if any(path_lower.endswith(ext) for ext in SKIP_EXTENSIONS):
        return False

    # Optionally skip PDFs
    if skip_pdfs and any(path_lower.endswith(ext) for ext in PDF_EXTENSIONS):
        return False

    return True


def extract_text(soup: BeautifulSoup) -> str:
    """Return clean, whitespace-normalised visible text from a page."""
    # Remove non-visible tags
    for tag in soup(["script", "style", "noscript", "head",
                     "header", "footer", "nav", "aside"]):
        tag.decompose()

    raw = soup.get_text(separator=" ")
    lines = (line.strip() for line in raw.splitlines())
    chunks = (phrase.strip() for line in lines for phrase in line.split("  "))
    return " ".join(chunk for chunk in chunks if chunk)


def extract_links(soup: BeautifulSoup, current_url: str) -> list[str]:
    """Return all absolute hrefs found on the page."""
    links = []
    for tag in soup.find_all("a", href=True):
        href = tag["href"].strip()
        absolute = urljoin(current_url, href)
        links.append(normalise_url(absolute))
    return links


def is_pdf_url(url: str) -> bool:
    """Return True if the URL path ends with .pdf (case-insensitive)."""
    return urlparse(url).path.lower().endswith(".pdf")


def extract_pdf_text(content: bytes) -> tuple[str, str]:
    """
    Extract all text from PDF bytes using pdfplumber.
    Returns (text, error). On success error is ''; on failure text is ''.
    """
    try:
        with pdfplumber.open(io.BytesIO(content)) as pdf:
            pages_text = []
            for i, page in enumerate(pdf.pages, start=1):
                page_text = page.extract_text() or ""
                if page_text.strip():
                    pages_text.append(f"[Page {i}] {page_text.strip()}")
            return " ".join(pages_text), ""
    except Exception as exc:
        return "", f"PDF parse error: {exc}"


def fetch_page(session: requests.Session, url: str, timeout: int = 15):
    """GET a URL; return (response | None, error_message | None)."""
    try:
        resp = session.get(url, timeout=timeout, allow_redirects=True)
        resp.raise_for_status()
        return resp, None
    except requests.exceptions.RequestException as exc:
        return None, str(exc)


# ── Core crawler ───────────────────────────────────────────────────────────────

def crawl(
    base_url: str,
    max_pages: int = 50,
    delay: float = 1.0,
    max_depth: int = 3,
    same_domain: bool = True,
    skip_pdfs: bool = False,
) -> list[dict]:
    """
    BFS crawl starting from *base_url*.
    Returns a list of dicts, one per visited URL.
    """
    base_url = normalise_url(base_url)
    base_domain = urlparse(base_url).netloc

    session = requests.Session()
    session.headers.update(DEFAULT_HEADERS)

    # Queue items: (url, depth)
    queue: deque[tuple[str, int]] = deque([(base_url, 0)])
    visited: set[str] = set()
    results: list[dict] = []

    log.info("Starting crawl → %s  (max_pages=%d, max_depth=%d)",
             base_url, max_pages, max_depth)

    while queue and len(results) < max_pages:
        url, depth = queue.popleft()

        if url in visited:
            continue
        visited.add(url)

        log.info("[%d/%d] depth=%d  %s", len(results) + 1, max_pages, depth, url)

        resp, error = fetch_page(session, url)
        scraped_at = datetime.utcnow().isoformat(timespec="seconds") + "Z"

        if error or resp is None:
            log.warning("  ✗ %s", error)
            results.append({
                "url": url,
                "depth": depth,
                "status_code": "",
                "title": "",
                "content_type": "",
                "text": "",
                "word_count": 0,
                "outbound_links": 0,
                "scraped_at": scraped_at,
                "error": error,
            })
            continue

        # ── PDF handling ──────────────────────────────────────────────────
        if is_pdf_url(url):
            log.info("  📄 PDF detected, extracting text…")
            text, pdf_error = extract_pdf_text(resp.content)
            word_count = len(text.split()) if text else 0
            results.append({
                "url": url,
                "depth": depth,
                "status_code": resp.status_code,
                "title": url.split("/")[-1],   # filename as title
                "content_type": "pdf",
                "text": text,
                "word_count": word_count,
                "outbound_links": 0,
                "scraped_at": scraped_at,
                "error": pdf_error,
            })
            log.info("  ✓ PDF words=%d", word_count)
            if delay > 0:
                time.sleep(delay)
            continue

        # ── HTML handling ─────────────────────────────────────────────────
        content_type = resp.headers.get("Content-Type", "")
        if "html" not in content_type:
            log.info("  ↷ Skipping non-HTML content-type: %s", content_type)
            continue

        soup = BeautifulSoup(resp.text, "lxml")

        title_tag = soup.find("title")
        title = title_tag.get_text(strip=True) if title_tag else ""

        text = extract_text(soup)
        word_count = len(text.split())

        links = extract_links(soup, url)
        outbound = 0
        if depth < max_depth:
            for link in links:
                if link not in visited and is_scrapable(
                        link, base_domain, same_domain, skip_pdfs):
                    queue.append((link, depth + 1))
                    outbound += 1

        results.append({
            "url": url,
            "depth": depth,
            "status_code": resp.status_code,
            "title": title,
            "content_type": "html",
            "text": text,
            "word_count": word_count,
            "outbound_links": outbound,
            "scraped_at": scraped_at,
            "error": "",
        })

        log.info("  ✓ title=%r  words=%d  new_links=%d", title, word_count, outbound)

        if delay > 0:
            time.sleep(delay)

    log.info("Crawl complete. Pages scraped: %d", len(results))
    return results


# ── CSV export ─────────────────────────────────────────────────────────────────

FIELDNAMES = [
    "url", "depth", "status_code", "title", "content_type",
    "text", "word_count", "outbound_links", "scraped_at", "error",
]


def save_to_csv(records: list[dict], output_path: str) -> None:
    """Write *records* to a UTF-8 CSV at *output_path*."""
    with open(output_path, "w", newline="", encoding="utf-8") as fh:
        writer = csv.DictWriter(fh, fieldnames=FIELDNAMES, extrasaction="ignore")
        writer.writeheader()
        writer.writerows(records)
    log.info("Saved %d rows → %s", len(records), output_path)


# ── CLI entry-point ────────────────────────────────────────────────────────────

def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Scrape public routes and text from a website, export to CSV."
    )
    parser.add_argument("--url", required=True, help="Base URL to start scraping")
    parser.add_argument("--output", default="scraped_data2.csv",
                        help="Output CSV filename (default: scraped_data.csv)")
    parser.add_argument("--max-pages", type=int, default=50,
                        help="Max pages to visit (default: 50)")
    parser.add_argument("--delay", type=float, default=1.0,
                        help="Seconds between requests (default: 1.0)")
    parser.add_argument("--depth", type=int, default=3,
                        help="Max crawl depth (default: 3)")
    parser.add_argument("--allow-external", action="store_true",
                        help="Follow links to other domains (default: False)")
    parser.add_argument("--skip-pdfs", action="store_true",
                        help="Do not extract text from PDF files (default: PDFs ARE scraped)")
    return parser.parse_args()


def main() -> None:
    args = parse_args()

    records = crawl(
        base_url=args.url,
        max_pages=args.max_pages,
        delay=args.delay,
        max_depth=args.depth,
        same_domain=not args.allow_external,
        skip_pdfs=args.skip_pdfs,
    )

    save_to_csv(records, args.output)
    print(f"\n✅  Done! {len(records)} pages saved to '{args.output}'")


if __name__ == "__main__":
    main()