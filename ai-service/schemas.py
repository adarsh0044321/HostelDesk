from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class IssueAnalysisRequest(BaseModel):
    title: str = Field(..., description="Issue title reported by student")
    description: str = Field(..., description="Detailed complaint description")
    category: Optional[str] = Field(None, description="Resident-selected category")
    block_name: Optional[str] = Field(None, description="Hostel block e.g. Block B")
    room_number: Optional[str] = Field(None, description="Room number e.g. 204")

class IssueAnalysisResponse(BaseModel):
    summary: str = Field(..., description="Structured executive summary for technicians")
    category: str = Field(..., description="Detected category e.g. PLUMBING, ELECTRICAL")
    priority: str = Field(..., description="P1_URGENT, P2_HIGH, P3_MEDIUM, P4_LOW")
    recommended_department: str = Field(..., description="Target department for routing")
    safety_hazard_note: Optional[str] = Field(None, description="Safety hazard warning if present")
    confidence: float = Field(..., description="Model confidence score between 0.0 and 1.0")
    is_fallback: bool = Field(False, description="Flag indicating fallback classifier")

class HistoricalComplaint(BaseModel):
    ticket_number: str
    room_number: str
    category: str
    description: str
    created_at: Optional[datetime] = None

class ClusterDetectionRequest(BaseModel):
    block_name: str
    complaints: List[HistoricalComplaint]

class ClusterInsight(BaseModel):
    block_name: str
    category: str
    complaint_count: int
    time_window_days: int
    pattern_description: str
    probable_cause: str
    recommended_action: str
    linked_tickets: List[str]

class ClusterDetectionResponse(BaseModel):
    insights: List[ClusterInsight]
