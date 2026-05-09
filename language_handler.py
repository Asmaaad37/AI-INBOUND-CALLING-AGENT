def detect_language_hint(text: str):
    """
    Simple heuristic (not ML-based, but effective for FYP)
    """

    urdu_words = ["hai", "ho", "aap", "apka", "kar", "nahi", "ky", "se"]

    if any(word in text.lower() for word in urdu_words):
        return "mixed"

    return "english"