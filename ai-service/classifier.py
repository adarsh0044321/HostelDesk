import re
from typing import Tuple, Optional
from schemas import IssueAnalysisResponse

# Keyword taxonomy for collegiate hostel infrastructure
TAXONOMY = {
    "ELECTRICAL": {
        "keywords": [
            "spark", "shock", "socket", "switch", "wiring", "wire", "regulator",
            "fuse", "mcb", "power", "light", "tube", "bulb", "fan", "short circuit",
            "smoke", "burnt", "burning", "ac", "air conditioner", "geyser"
        ],
        "urgent_triggers": ["spark", "shock", "smoke", "fire", "short circuit", "burnt"],
        "dept": "ELECTRICAL",
        "safety": "Active electrical sparking/shock hazard detected. Advise resident not to touch fixture."
    },
    "PLUMBING": {
        "keywords": [
            "leak", "leaking", "leakage", "water", "pipe", "tap", "faucet", "drain",
            "drainage", "sewage", "flush", "commode", "toilet", "washbasin", "sink",
            "overflow", "clog", "choke", "dripping", "damp", "seepage", "valve"
        ],
        "urgent_triggers": ["heavily", "burst", "flood", "overflow", "ceiling leak"],
        "dept": "PLUMBING",
        "safety": "Water leakage detected near electrical points or ceiling, potential slip and circuit hazard."
    },
    "CARPENTRY": {
        "keywords": [
            "door", "lock", "latch", "hinge", "handle", "window", "pane", "glass",
            "bed", "cupboard", "almirah", "wardrobe", "drawer", "table", "chair", "wood"
        ],
        "urgent_triggers": ["lock broken", "door jammed", "cannot enter", "cannot lock"],
        "dept": "CARPENTRY",
        "safety": None
    },
    "CLEANING": {
        "keywords": [
            "clean", "cleaning", "dust", "garbage", "trash", "waste", "litter",
            "smell", "stink", "stain", "dirty", "sweep", "mop", "pest", "cockroach", "rodent"
        ],
        "urgent_triggers": ["dead animal", "severe foul smell"],
        "dept": "CLEANING",
        "safety": None
    },
    "INTERNET": {
        "keywords": [
            "wifi", "wi-fi", "internet", "lan", "ethernet", "router", "network",
            "connection", "speed", "slow", "disconnect", "access point"
        ],
        "urgent_triggers": [],
        "dept": "INTERNET",
        "safety": None
    },
    "CIVIL": {
        "keywords": [
            "wall", "ceiling", "crack", "plaster", "cement", "tile", "floor",
            "masonry", "paint", "peeling"
        ],
        "urgent_triggers": ["ceiling collapse", "falling plaster"],
        "dept": "CIVIL",
        "safety": "Loose plaster or masonry risk"
    }
}

def analyze_text(title: str, description: str, resident_category: Optional[str] = None) -> IssueAnalysisResponse:
    combined = f"{title} {description}".lower()
    scores = {}

    for cat, data in TAXONOMY.items():
        score = 0
        for kw in data["keywords"]:
            if re.search(r'\b' + re.escape(kw) + r'\b', combined):
                score += 1.0
            elif kw in combined:
                score += 0.5
        scores[cat] = score

    best_cat = max(scores, key=scores.get)
    best_score = scores[best_cat]

    if best_score == 0:
        if resident_category and resident_category.upper() in TAXONOMY:
            best_cat = resident_category.upper()
            confidence = 0.70
        else:
            best_cat = "GENERAL"
            confidence = 0.50
    else:
        confidence = min(0.96, 0.70 + (best_score * 0.05))

    cat_data = TAXONOMY.get(best_cat, {"dept": "GENERAL", "urgent_triggers": [], "safety": None})
    recommended_dept = cat_data.get("dept", "GENERAL")

    # Determine priority
    is_urgent = any(trigger in combined for trigger in cat_data.get("urgent_triggers", []))
    if is_urgent:
        priority = "P1_URGENT"
    elif best_score >= 2 or best_cat in ["ELECTRICAL", "PLUMBING"]:
        priority = "P2_HIGH"
    elif best_cat in ["CARPENTRY", "CLEANING", "INTERNET"]:
        priority = "P3_MEDIUM"
    else:
        priority = "P3_MEDIUM"

    # Safety note
    safety_note = None
    if is_urgent or cat_data.get("safety"):
        safety_note = cat_data.get("safety")
        if "ceiling" in combined and "leak" in combined and ("light" in combined or "switch" in combined):
            safety_note = "Water dripping near ceiling light fitting. Risk of electrical grounding."

    summary = f"{title.strip().capitalize()}: {best_cat.lower()} issue detected for {recommended_dept} department triage."

    return IssueAnalysisResponse(
        summary=summary,
        category=best_cat,
        priority=priority,
        recommended_department=recommended_dept,
        safety_hazard_note=safety_note,
        confidence=round(confidence, 3),
        is_fallback=False
    )
