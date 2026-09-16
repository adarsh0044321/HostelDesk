from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from schemas import (
    IssueAnalysisRequest,
    IssueAnalysisResponse,
    ClusterDetectionRequest,
    ClusterDetectionResponse
)
from classifier import analyze_text
from clustering import detect_clusters

app = FastAPI(
    title="HostelDesk AI Microservice",
    description="Intelligent natural-language classification, SLA priority inference, and recurring infrastructure pattern clustering for university residences.",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/health")
def health_check():
    return {"status": "UP", "service": "HostelDesk-AI", "version": "1.0.0"}

@app.post("/ai/analyze-issue", response_model=IssueAnalysisResponse)
def analyze_issue(req: IssueAnalysisRequest):
    if not req.title.strip() and not req.description.strip():
        raise HTTPException(status_code=400, detail="Title and description cannot both be blank")

    analysis = analyze_text(req.title, req.description, req.category)
    return analysis

@app.post("/ai/detect-recurring-issues", response_model=ClusterDetectionResponse)
def detect_recurring_issues(req: ClusterDetectionRequest):
    insights = detect_clusters(req.block_name, req.complaints)
    return ClusterDetectionResponse(insights=insights)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
