# PlantUML Component Diagram

This directory contains a PlantUML script (`component-diagram.puml`) that shows the high-level component relationships in the gRPC MCP Bridge project.

## Diagram Overview

The component diagram illustrates the relationships between:
- Large Language Model (LLM)
- MCP Client
- MCP Bridge
- gRPC Server

The diagram focuses on the external relationships between these components without showing their internal details.

## Generating the Image

To generate the image from the PlantUML script, you can use one of the following methods:

### Method 1: Using PlantUML Command Line

If you have PlantUML installed locally:

```bash
plantuml component-diagram.puml
```

This will generate `component-diagram.png` in the same directory.

### Method 2: Using Online PlantUML Server

1. Copy the content of `component-diagram.puml`
2. Go to [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/)
3. Paste the content
4. The diagram will be rendered, and you can download it as PNG, SVG, or other formats

### Method 3: Using IntelliJ IDEA PlantUML Plugin

If you're using IntelliJ IDEA:

1. Install the PlantUML Integration plugin
2. Open `component-diagram.puml`
3. Right-click and select "PlantUML > Export Diagram"
4. Choose your preferred format (PNG, SVG, etc.)

## Component Relationships

The diagram shows the following relationships:
1. LLM <--> MCP Client: Natural language interaction
2. MCP Client <--> MCP Bridge: Communication using MCP protocol
3. MCP Bridge <--> gRPC Server: Communication using gRPC protocol

## Data Flow

The typical data flow through the system is:
1. User sends a request to the MCP Client
2. MCP Client forwards the request to the LLM
3. LLM decides to call a tool via the MCP Bridge
4. MCP Bridge converts the request to gRPC format
5. gRPC Server processes the request and returns the result
6. The result flows back through the same path to the user
