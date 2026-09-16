from typing import List
from collections import defaultdict
from schemas import HistoricalComplaint, ClusterInsight

def detect_clusters(block_name: str, complaints: List[HistoricalComplaint]) -> List[ClusterInsight]:
    if not complaints:
        return []

    # Group by category
    by_cat = defaultdict(list)
    for c in complaints:
        by_cat[c.category.upper()].append(c)

    insights = []
    for cat, items in by_cat.items():
        if len(items) >= 3:
            rooms = sorted(list(set(c.room_number for c in items if c.room_number)))
            room_str = f"Rooms {rooms[0]}–{rooms[-1]}" if len(rooms) > 1 else f"Room {rooms[0]}"

            pattern_desc = f"{len(items)} {cat.lower()} complaints recorded across {room_str} in {block_name}."

            if cat == "PLUMBING":
                cause = "Shared vertical drainage stack line blockage or riser shaft pressure imbalance."
                action = "Inspect vertical riser shaft and drain manifold above corridor ceiling."
            elif cat == "ELECTRICAL":
                cause = "Sub-distribution board circuit overload or loose phase connection on floor feeder."
                action = "Inspect floor MCB distribution panel and load balance floor phases."
            elif cat == "INTERNET":
                cause = "Corridor PoE switch port flapping or local Access Point DHCP exhaustion."
                action = "Reboot corridor access point and inspect upstream PoE switch uplink."
            else:
                cause = "Repeated maintenance demand in localized residential cluster."
                action = "Dispatch senior supervisor to conduct on-site block survey."

            insights.append(ClusterInsight(
                block_name=block_name,
                category=cat,
                complaint_count=len(items),
                time_window_days=14,
                pattern_description=pattern_desc,
                probable_cause=cause,
                recommended_action=action,
                linked_tickets=[c.ticket_number for c in items]
            ))

    return insights
