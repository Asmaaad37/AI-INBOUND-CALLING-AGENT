import os
from dotenv import load_dotenv

load_dotenv()
NEO4J_URI = os.getenv("NEO4J_URI")
NEO4J_USERNAME = os.getenv("NEO4J_USERNAME")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

# Neo4j Driver
import neo4j

neo4j_driver = neo4j.GraphDatabase.driver(NEO4J_URI,
                auth=(NEO4J_USERNAME, NEO4J_PASSWORD))

# LLM and Embedding Model
from neo4j_graphrag.llm import OpenAILLM
from neo4j_graphrag.embeddings.openai import OpenAIEmbeddings

llm=OpenAILLM(
   model_name="gpt-4o-mini",
   model_params={
       "response_format": {"type": "json_object"}, # use json_object formatting for best results
       "temperature": 0 # turning temperature down for more deterministic results
   }
)

# Core Academic Entities
academic_nodes = [
    "Department",
    "Program",
    "Course",
    "Semester",
    "Faculty"
]

# Fee System
fee_nodes = [
    "FeeStructure",
    "FeeComponent",
    "FeeType",
    "StudyMode"
]

# Admission & Metadata
admission_nodes = [
    "Eligibility",
    "Requirement",
    "Duration",
    "CreditInfo"
]

# Optional (for GraphRAG context chunks)
rag_nodes = [
    "Chunk"
]

node_labels = academic_nodes + fee_nodes + admission_nodes + rag_nodes

rel_types = [

    # Academic Structure
    "OFFERS",              # Department → Program
    "HAS_SEMESTER",        # Program → Semester
    "HAS_COURSE",          # Semester → Course
    "HAS_PREREQUISITE",    # Course → Course
    "TAUGHT_BY",           # Course → Faculty

    # Program Metadata
    "HAS_ELIGIBILITY",     # Program → Eligibility
    "HAS_DURATION",        # Program → Duration
    "HAS_CREDIT_INFO",     # Program → CreditInfo

    # Fee Structure
    "HAS_FEE_STRUCTURE",   # Program → FeeStructure
    "INCLUDES_FEE",        # FeeStructure → FeeComponent
    "OF_TYPE",             # FeeComponent → FeeType
    "APPLIES_TO",          # FeeComponent → StudyMode

    # GraphRAG Linking
    "FROM_CHUNK"           # Entity → Chunk
]

patterns = [
    ("Department", "OFFERS", "Program"),
    ("Program", "HAS_SEMESTER", "Semester"),
    ("Semester", "HAS_COURSE", "Course"),
    ("Course", "HAS_PREREQUISITE", "Course"),
    ("Course", "TAUGHT_BY", "Faculty"),

    ("Program", "HAS_FEE_STRUCTURE", "FeeStructure"),
    ("FeeStructure", "INCLUDES_FEE", "FeeComponent"),
    ("FeeComponent", "OF_TYPE", "FeeType"),
    ("FeeComponent", "APPLIES_TO", "StudyMode"),

    ("Program", "HAS_ELIGIBILITY", "Eligibility")
]



#create text embedder
embedder = OpenAIEmbeddings()

# define prompt template
prompt_template = '''
You are an AI system designed to extract structured academic and admission-related knowledge 
from university documents and convert it into a Knowledge Graph.

Your task is to:

1. Identify entities (nodes) from the text.
2. Assign each entity a valid type (label).
3. Extract relationships between entities.
4. Maintain correct direction of relationships.

---

### STRICT INSTRUCTIONS:

- ONLY use the provided node labels and relationship types.
- DO NOT invent new labels or relationships.
- DO NOT include unnecessary or duplicate entities.
- Normalize entity names (e.g., "BS Computer Science" should be consistent).
- Each node MUST have a unique string ID.
- Relationships MUST correctly reference node IDs.

---

### DOMAIN UNDERSTANDING:

The text may include:

- Programs (e.g., BS Computer Science)
- Departments (e.g., Computer Science Department)
- Courses and semesters
- Fee structures (tuition, admission fee, etc.)
- Study modes (Regular, Self Support, Weekend)
- Eligibility criteria

---

### OUTPUT FORMAT (STRICT JSON ONLY):

{
  "nodes": [
    {
      "id": "0",
      "label": "Program",
      "properties": {
        "name": "BS Computer Science"
      }
    }
  ],
  "relationships": [
    {
      "type": "HAS_FEE_STRUCTURE",
      "start_node_id": "0",
      "end_node_id": "1",
      "properties": {
        "details": "Program has fee structure"
      }
    }
  ]
}

---

### ALLOWED SCHEMA:

{schema}

---

### EXAMPLES:

Example Input:
"BS Computer Science program has a tuition fee of 35150 per semester."

Example Output:
{
  "nodes": [
    {"id": "0", "label": "Program", "properties": {"name": "BS Computer Science"}},
    {"id": "1", "label": "FeeComponent", "properties": {"amount": "35150"}},
    {"id": "2", "label": "FeeType", "properties": {"name": "Tuition Fee"}}
  ],
  "relationships": [
    {"type": "INCLUDES_FEE", "start_node_id": "0", "end_node_id": "1", "properties": {}},
    {"type": "OF_TYPE", "start_node_id": "1", "end_node_id": "2", "properties": {}}
  ]
}

---

### INPUT TEXT:

{text}
'''

# Knowledge Graph Builder
from neo4j_graphrag.experimental.components.text_splitters.fixed_size_splitter import FixedSizeSplitter
from neo4j_graphrag.experimental.pipeline.kg_builder import SimpleKGPipeline

kg_builder_pdf = SimpleKGPipeline(
   llm=ex_llm,
   driver=driver,
   text_splitter=FixedSizeSplitter(chunk_size=500, chunk_overlap=100),
   embedder=embedder,
   entities=node_labels,
   relations=rel_types,
   prompt_template=prompt_template,
   from_pdf=True
)

pdf_file_paths = ['truncated-pdfs/biomolecules-11-00928-v2-trunc.pdf',
            'truncated-pdfs/GAP-between-patients-and-clinicians_2023_Best-Practice-trunc.pdf',
            'truncated-pdfs/pgpm-13-39-trunc.pdf']

for path in pdf_file_paths:
    print(f"Processing : {path}")
    pdf_result = await kg_builder_pdf.run_async(file_path=path)
    print(f"Result: {pdf_result}")



from neo4j_graphrag.indexes import create_vector_index

create_vector_index(driver, name="text_embeddings", label="Chunk",
                   embedding_property="embedding", dimensions=384, similarity_fn="cosine")

# Vector Retriever
from neo4j_graphrag.retrievers import VectorRetriever

vector_retriever = VectorRetriever(
   driver,
   index_name="text_embeddings",
   embedder=embedder,
   return_properties=["text"],
)

# GraphRAG Vector Cypher Retriever
from neo4j_graphrag.retrievers import VectorCypherRetriever

graph_retriever = VectorCypherRetriever(
    driver,
    index_name="text_embeddings",
    embedder=embedder,
    retrieval_query="""
// Step 1: Start from retrieved chunk
WITH node AS chunk

// Step 2: Link chunk to entities
MATCH (chunk)<-[:FROM_CHUNK]-(p:Program)

// Step 3: Traverse structured academic graph
OPTIONAL MATCH (p)-[:BELONGS_TO]->(d:Department)
OPTIONAL MATCH (p)-[:HAS_SEMESTER]->(s:Semester)
OPTIONAL MATCH (s)-[:HAS_COURSE]->(c:Course)
OPTIONAL MATCH (p)-[:HAS_FEE_STRUCTURE]->(fs:FeeStructure)
OPTIONAL MATCH (fs)-[:INCLUDES_FEE]->(fc:FeeComponent)
OPTIONAL MATCH (fc)-[:OF_TYPE]->(ft:FeeType)
OPTIONAL MATCH (ft)-[:APPLIES_TO]->(sm:StudyMode)

// Step 4: Collect structured info
WITH chunk, p, d, s, c, fs, fc, ft, sm

RETURN 
chunk.text + '\n' +

'Program: ' + coalesce(p.name,'') + '\n' +
'Department: ' + coalesce(d.name,'') + '\n' +
'Semester: ' + coalesce(s.name,'') + '\n' +
'Course: ' + coalesce(c.name,'') + '\n' +
'Fee Component: ' + coalesce(fc.name,'') + '\n' +
'Fee Type: ' + coalesce(ft.name,'') + '\n' +
'Study Mode: ' + coalesce(sm.name,'') AS info
"""
)

llm = LLM(model_name="gpt-4o",  model_params={"temperature": 0.0})

rag_template = RagTemplate(template='''
You are a university academic assistant.

Answer the question using ONLY the provided context.
Focus on:
- Program details
- Fees (components, type, study mode)
- Courses and semesters

If exact data is missing, say "Not found in context".

# Question:
{query_text}

# Context:
{context}

# Answer:
''', expected_inputs=['query_text', 'context'])

vector_rag  = GraphRAG(llm=llm, retriever=vector_retriever, prompt_template=rag_template)

graph_rag = GraphRAG(llm=llm, retriever=graph_retriever, prompt_template=rag_template)

q = "What is the complete fee structure for BS Computer Science including all fee components?"

print("=== VECTOR RAG ===")
print(vector_rag.search(q, retriever_config={'top_k':5}).answer)

print("\n=== GRAPH RAG ===")
print(graph_rag.search(q, retriever_config={'top_k':5}).answer)