import pytest
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_health_endpoint():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "UP"
    assert data["service"] == "HostelDesk-AI"

def test_plumbing_classification():
    payload = {
        "title": "Bathroom water pipe leakage",
        "description": "Heavy water leakage from the ceiling tap in room 204 near washbasin.",
        "category": "PLUMBING",
        "block_name": "Block B",
        "room_number": "204"
    }
    response = client.post("/ai/analyze-issue", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["category"] == "PLUMBING"
    assert data["recommended_department"] == "PLUMBING"
    assert data["confidence"] >= 0.70
    assert "leak" in data["summary"].lower() or "plumbing" in data["summary"].lower()

def test_electrical_spark_hazard_classification():
    payload = {
        "title": "Study table socket sparking",
        "description": "Sparks and burning smoke coming from the power socket when plug inserted.",
        "category": "ELECTRICAL",
        "block_name": "Block A",
        "room_number": "312"
    }
    response = client.post("/ai/analyze-issue", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["category"] == "ELECTRICAL"
    assert data["priority"] == "P1_URGENT"
    assert data["safety_hazard_note"] is not None

def test_recurring_cluster_detection():
    payload = {
        "block_name": "Block B",
        "complaints": [
            {"ticket_number": "HD-1001", "room_number": "201", "category": "PLUMBING", "description": "Bathroom drain choke"},
            {"ticket_number": "HD-1002", "room_number": "202", "category": "PLUMBING", "description": "Sink water backup"},
            {"ticket_number": "HD-1003", "room_number": "203", "category": "PLUMBING", "description": "Ceiling pipe leak"},
            {"ticket_number": "HD-1004", "room_number": "204", "category": "PLUMBING", "description": "Water seepage"}
        ]
    }
    response = client.post("/ai/detect-recurring-issues", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert len(data["insights"]) >= 1
    insight = data["insights"][0]
    assert insight["category"] == "PLUMBING"
    assert insight["complaint_count"] == 4
    assert "Block B" in insight["block_name"]
