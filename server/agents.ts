import { agents, tasks, agentLogs, type Agent, type Task, type AgentLog } from "@shared/schema";
import { db } from "./db";
import { eq, and, desc } from "drizzle-orm";
import { v4 as uuidv4 } from "uuid";
import { promises as fs } from "fs";
import path from "path";
import { exec } from "child_process";
import { promisify } from "util";

const execAsync = promisify(exec);

export interface IAgentStorage {
  // Agent operations
  getAgents(userId: string): Promise<Agent[]>;
  getAgent(id: string, userId: string): Promise<Agent | undefined>;
  createAgent(agent: Omit<Agent, "id" | "createdAt" | "updatedAt">): Promise<Agent>;
  updateAgent(id: string, updates: Partial<Agent>): Promise<Agent>;
  deleteAgent(id: string, userId: string): Promise<void>;
  
  // Task operations
  getTasks(userId: string): Promise<Task[]>;
  getTask(id: string, userId: string): Promise<Task | undefined>;
  createTask(task: Omit<Task, "id" | "createdAt">): Promise<Task>;
  updateTask(id: string, updates: Partial<Task>): Promise<Task>;
  
  // Log operations
  getAgentLogs(agentId: string): Promise<AgentLog[]>;
  addAgentLog(log: Omit<AgentLog, "id" | "timestamp">): Promise<AgentLog>;
}

export class AgentStorage implements IAgentStorage {
  async getAgents(userId: string): Promise<Agent[]> {
    return await db.select().from(agents).where(eq(agents.userId, userId)).orderBy(desc(agents.createdAt));
  }

  async getAgent(id: string, userId?: string): Promise<Agent | undefined> {
    if (userId) {
      const [agent] = await db.select().from(agents).where(and(eq(agents.id, id), eq(agents.userId, userId)));
      return agent;
    } else {
      const [agent] = await db.select().from(agents).where(eq(agents.id, id));
      return agent;
    }
  }

  async createAgent(agentData: Omit<Agent, "id" | "createdAt" | "updatedAt">): Promise<Agent> {
    const id = uuidv4();
    const [agent] = await db.insert(agents).values({
      ...agentData,
      id,
      createdAt: new Date(),
      updatedAt: new Date(),
    }).returning();
    return agent;
  }

  async updateAgent(id: string, updates: Partial<Agent>): Promise<Agent> {
    const [agent] = await db.update(agents)
      .set({ ...updates, updatedAt: new Date() })
      .where(eq(agents.id, id))
      .returning();
    return agent;
  }

  async deleteAgent(id: string, userId: string): Promise<void> {
    await db.delete(agents).where(and(eq(agents.id, id), eq(agents.userId, userId)));
  }

  async getTasks(userId: string): Promise<Task[]> {
    return await db.select().from(tasks).where(eq(tasks.userId, userId)).orderBy(desc(tasks.createdAt));
  }

  async getTask(id: string, userId: string): Promise<Task | undefined> {
    const [task] = await db.select().from(tasks).where(and(eq(tasks.id, id), eq(tasks.userId, userId)));
    return task;
  }

  async createTask(taskData: Omit<Task, "id" | "createdAt">): Promise<Task> {
    const id = uuidv4();
    const [task] = await db.insert(tasks).values({
      ...taskData,
      id,
      createdAt: new Date(),
    }).returning();
    return task;
  }

  async updateTask(id: string, updates: Partial<Task>): Promise<Task> {
    const [task] = await db.update(tasks)
      .set(updates)
      .where(eq(tasks.id, id))
      .returning();
    return task;
  }

  async getAgentLogs(agentId: string): Promise<AgentLog[]> {
    return await db.select().from(agentLogs).where(eq(agentLogs.agentId, agentId)).orderBy(desc(agentLogs.timestamp));
  }

  async addAgentLog(logData: Omit<AgentLog, "id" | "timestamp">): Promise<AgentLog> {
    const id = uuidv4();
    const [log] = await db.insert(agentLogs).values({
      ...logData,
      id,
      timestamp: new Date(),
    }).returning();
    return log;
  }
}

// Agent execution engine
export class AgentExecutor {
  private storage: IAgentStorage;
  private runningAgents: Map<string, boolean> = new Map();

  constructor(storage: IAgentStorage) {
    this.storage = storage;
  }

  async executeTask(task: Task): Promise<void> {
    try {
      await this.storage.updateTask(task.id, { status: "running", progress: 0 });
      await this.storage.addAgentLog({
        agentId: task.agentId!,
        level: "info",
        message: `Starting task: ${task.name}`,
        details: JSON.stringify(task.data),
      });

      const result = await this.executeByType(task);
      
      await this.storage.updateTask(task.id, { 
        status: "completed", 
        progress: 100,
        result: result,
        completedAt: new Date()
      });

      await this.storage.addAgentLog({
        agentId: task.agentId!,
        level: "success",
        message: `Task completed: ${task.name}`,
        details: JSON.stringify(result),
      });

    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : "Unknown error";
      
      await this.storage.updateTask(task.id, { 
        status: "failed", 
        error: errorMessage 
      });

      await this.storage.addAgentLog({
        agentId: task.agentId!,
        level: "error",
        message: `Task failed: ${task.name}`,
        details: errorMessage,
      });
    }
  }

  private async executeByType(task: Task): Promise<any> {
    const data = task.data as any;
    
    switch (task.type) {
      case 'code-saver':
        return await this.saveCodeToFile(data);
      case 'document-manager':
        return await this.createDocument(data);
      case 'web-scraper':
        return await this.scrapeWebData(data);
      case 'data-processor':
        return await this.processDataFile(data);
      case 'file-organizer':
        return await this.organizeFiles(data);
      default:
        throw new Error(`Unknown task type: ${task.type}`);
    }
  }

  private async saveCodeToFile(data: any): Promise<any> {
    const { code, filename, format, folder } = data;
    
    // Create folder if it doesn't exist
    const folderPath = folder || './generated-files';
    await fs.mkdir(folderPath, { recursive: true });
    
    let finalFilename = filename;
    if (!filename.includes('.')) {
      finalFilename = `${filename}.${format}`;
    }
    
    const filePath = path.join(folderPath, finalFilename);
    
    let content = code;
    if (format === 'md') {
      content = `# ${filename}\n\n\`\`\`\n${code}\n\`\`\`\n`;
    } else if (format === 'html') {
      content = `<!DOCTYPE html>\n<html>\n<head>\n<title>${filename}</title>\n</head>\n<body>\n<pre><code>${code}</code></pre>\n</body>\n</html>`;
    }
    
    await fs.writeFile(filePath, content, 'utf-8');
    
    return {
      success: true,
      filePath: filePath,
      filename: finalFilename,
      size: Buffer.byteLength(content, 'utf-8'),
      format: format
    };
  }

  private async createDocument(data: any): Promise<any> {
    const { title, content, format, template } = data;
    
    const folderPath = './generated-documents';
    await fs.mkdir(folderPath, { recursive: true });
    
    const filename = `${title.replace(/[^a-zA-Z0-9]/g, '_')}.${format}`;
    const filePath = path.join(folderPath, filename);
    
    let docContent = content;
    
    if (format === 'md') {
      docContent = `# ${title}\n\n${content}\n\n---\n*Generated on ${new Date().toISOString()}*`;
    } else if (format === 'html') {
      docContent = `<!DOCTYPE html>\n<html>\n<head>\n<title>${title}</title>\n<style>body{font-family:Arial,sans-serif;max-width:800px;margin:0 auto;padding:20px;}</style>\n</head>\n<body>\n<h1>${title}</h1>\n${content.replace(/\n/g, '<br>')}\n<hr>\n<p><em>Generated on ${new Date().toISOString()}</em></p>\n</body>\n</html>`;
    }
    
    await fs.writeFile(filePath, docContent, 'utf-8');
    
    return {
      success: true,
      filePath: filePath,
      filename: filename,
      title: title,
      format: format,
      size: Buffer.byteLength(docContent, 'utf-8')
    };
  }

  private async scrapeWebData(data: any): Promise<any> {
    const { url, selector, output } = data;
    
    // For demo purposes, we'll simulate web scraping
    // In a real implementation, you'd use puppeteer or similar
    const scrapedData = {
      url: url,
      timestamp: new Date().toISOString(),
      data: {
        title: "Example Page Title",
        content: "Example scraped content",
        links: ["https://example.com/link1", "https://example.com/link2"]
      }
    };
    
    const folderPath = './scraped-data';
    await fs.mkdir(folderPath, { recursive: true });
    
    const filename = `scraped_${Date.now()}.${output}`;
    const filePath = path.join(folderPath, filename);
    
    let content = '';
    if (output === 'json') {
      content = JSON.stringify(scrapedData, null, 2);
    } else if (output === 'csv') {
      content = 'url,title,content\n' + `"${scrapedData.url}","${scrapedData.data.title}","${scrapedData.data.content}"`;
    } else {
      content = `URL: ${scrapedData.url}\nTitle: ${scrapedData.data.title}\nContent: ${scrapedData.data.content}`;
    }
    
    await fs.writeFile(filePath, content, 'utf-8');
    
    return {
      success: true,
      filePath: filePath,
      filename: filename,
      dataExtracted: scrapedData,
      format: output
    };
  }

  private async processDataFile(data: any): Promise<any> {
    const { inputFile, operation, outputFormat } = data;
    
    // Simulate data processing
    const processedData = {
      operation: operation,
      inputFile: inputFile,
      outputFormat: outputFormat,
      timestamp: new Date().toISOString(),
      recordsProcessed: 100,
      recordsFiltered: 85
    };
    
    const folderPath = './processed-data';
    await fs.mkdir(folderPath, { recursive: true });
    
    const filename = `processed_${Date.now()}.${outputFormat}`;
    const filePath = path.join(folderPath, filename);
    
    let content = '';
    if (outputFormat === 'json') {
      content = JSON.stringify(processedData, null, 2);
    } else if (outputFormat === 'csv') {
      content = 'operation,input_file,records_processed,records_filtered\n' + 
                `"${operation}","${inputFile}",${processedData.recordsProcessed},${processedData.recordsFiltered}`;
    }
    
    await fs.writeFile(filePath, content, 'utf-8');
    
    return {
      success: true,
      filePath: filePath,
      filename: filename,
      operation: operation,
      stats: processedData
    };
  }

  private async organizeFiles(data: any): Promise<any> {
    const { sourceFolder, rules, action } = data;
    
    // Simulate file organization
    const organizationResult = {
      sourceFolder: sourceFolder,
      action: action,
      rules: rules,
      timestamp: new Date().toISOString(),
      filesProcessed: 25,
      filesOrganized: 20,
      foldersCreated: 3
    };
    
    // Create a report
    const folderPath = './file-organization-reports';
    await fs.mkdir(folderPath, { recursive: true });
    
    const filename = `organization_report_${Date.now()}.json`;
    const filePath = path.join(folderPath, filename);
    
    await fs.writeFile(filePath, JSON.stringify(organizationResult, null, 2), 'utf-8');
    
    return {
      success: true,
      reportPath: filePath,
      stats: organizationResult
    };
  }

  async startAgent(agentId: string): Promise<void> {
    if (this.runningAgents.get(agentId)) {
      throw new Error('Agent is already running');
    }
    
    this.runningAgents.set(agentId, true);
    await this.storage.updateAgent(agentId, { status: "running" });
    
    // Get the agent to find its userId
    const agent = await this.storage.getAgent(agentId);
    if (!agent) {
      throw new Error('Agent not found');
    }
    
    // Process pending tasks for this agent
    const allTasks = await this.storage.getTasks(agent.userId || '');
    const pendingTasks = allTasks.filter(task => task.agentId === agentId && task.status === 'pending');
    
    for (const task of pendingTasks) {
      if (this.runningAgents.get(agentId)) {
        await this.executeTask(task);
      }
    }
    
    this.runningAgents.set(agentId, false);
    await this.storage.updateAgent(agentId, { status: "idle", lastRun: new Date() });
  }

  async stopAgent(agentId: string): Promise<void> {
    this.runningAgents.set(agentId, false);
    await this.storage.updateAgent(agentId, { status: "idle" });
  }

  async pauseAgent(agentId: string): Promise<void> {
    this.runningAgents.set(agentId, false);
    await this.storage.updateAgent(agentId, { status: "paused" });
  }
}

export const agentStorage = new AgentStorage();
export const agentExecutor = new AgentExecutor(agentStorage);