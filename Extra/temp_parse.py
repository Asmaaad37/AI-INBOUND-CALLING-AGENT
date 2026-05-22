import csv, re
from pathlib import Path

path = Path(r"w:\7th Sem\FYP\Project\AI-INBOUND-CALLING-AGENT\code\Fee_Structure.csv")

with path.open('r', encoding='utf-8', newline='') as f:
    reader = csv.DictReader(f)
    rows = list(reader)

if not rows:
    raise SystemExit('No rows')

row = rows[0]
text = row.get('text','')
# split into page segments by detecting [Page N] markers, possibly preceded by a page number
segments = re.split(r"(?=\d*\s*\[Page \d+\])", text)

new_rows = []
for seg in segments:
    seg = seg.strip()
    if not seg:
        continue
    # Find first [Page N]
    m = re.search(r"\[Page (\d+)\]", seg)
    page = m.group(1) if m else ''
    # Remove leading page number and marker
    content = re.sub(r"^\d*\s*\[Page \d+\]\s*", '', seg).strip()
    lines = [l.strip() for l in content.splitlines() if l.strip()]
    section = lines[0] if lines else ''
    rest = '\n'.join(lines[1:])
    new = dict(row)
    new['page'] = page
    new['section'] = section
    new['text'] = rest
    new_rows.append(new)

fieldnames = ['url','depth','status_code','title','content_type','page','section','text','word_count','outbound_links','scraped_at','error']
with path.open('w', encoding='utf-8', newline='') as f:
    writer = csv.DictWriter(f, fieldnames=fieldnames)
    writer.writeheader()
    for r in new_rows:
        writer.writerow(r)

print(f"Wrote {len(new_rows)} rows")
