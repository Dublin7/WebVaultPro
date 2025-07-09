import type { Express } from "express";
import { createServer, type Server } from "http";
import { storage } from "./storage";
import { setupAuth, isAuthenticated } from "./replitAuth";
import { agentStorage, agentExecutor } from "./agents";
import { insertAgentSchema, insertTaskSchema } from "@shared/schema";
import { v4 as uuidv4 } from "uuid";

export async function registerRoutes(app: Express): Promise<Server> {
  // Auth middleware
  await setupAuth(app);

  // Auth routes
  app.get('/api/auth/user', isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const user = await storage.getUser(userId);
      res.json(user);
    } catch (error) {
      console.error("Error fetching user:", error);
      res.status(500).json({ message: "Failed to fetch user" });
    }
  });

  // AI Agents routes
  app.get("/api/agents", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const agents = await agentStorage.getAgents(userId);
      res.json(agents);
    } catch (error) {
      console.error("Error fetching agents:", error);
      res.status(500).json({ message: "Failed to fetch agents" });
    }
  });

  app.post("/api/agents", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const agentData = {
        ...req.body,
        userId,
        id: uuidv4()
      };
      
      const agent = await agentStorage.createAgent(agentData);
      res.status(201).json(agent);
    } catch (error) {
      console.error("Error creating agent:", error);
      res.status(500).json({ message: "Failed to create agent" });
    }
  });

  app.post("/api/agents/:id/start", isAuthenticated, async (req: any, res) => {
    try {
      const { id } = req.params;
      const userId = req.user.claims.sub;
      
      const agent = await agentStorage.getAgent(id, userId);
      if (!agent) {
        return res.status(404).json({ message: "Agent not found" });
      }
      
      await agentExecutor.startAgent(id);
      res.json({ message: "Agent started successfully" });
    } catch (error) {
      console.error("Error starting agent:", error);
      res.status(500).json({ message: "Failed to start agent" });
    }
  });

  app.post("/api/agents/:id/stop", isAuthenticated, async (req: any, res) => {
    try {
      const { id } = req.params;
      const userId = req.user.claims.sub;
      
      const agent = await agentStorage.getAgent(id, userId);
      if (!agent) {
        return res.status(404).json({ message: "Agent not found" });
      }
      
      await agentExecutor.stopAgent(id);
      res.json({ message: "Agent stopped successfully" });
    } catch (error) {
      console.error("Error stopping agent:", error);
      res.status(500).json({ message: "Failed to stop agent" });
    }
  });

  app.post("/api/agents/:id/pause", isAuthenticated, async (req: any, res) => {
    try {
      const { id } = req.params;
      const userId = req.user.claims.sub;
      
      const agent = await agentStorage.getAgent(id, userId);
      if (!agent) {
        return res.status(404).json({ message: "Agent not found" });
      }
      
      await agentExecutor.pauseAgent(id);
      res.json({ message: "Agent paused successfully" });
    } catch (error) {
      console.error("Error pausing agent:", error);
      res.status(500).json({ message: "Failed to pause agent" });
    }
  });

  app.get("/api/tasks", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const tasks = await agentStorage.getTasks(userId);
      res.json(tasks);
    } catch (error) {
      console.error("Error fetching tasks:", error);
      res.status(500).json({ message: "Failed to fetch tasks" });
    }
  });

  app.post("/api/tasks", isAuthenticated, async (req: any, res) => {
    try {
      const userId = req.user.claims.sub;
      const taskData = {
        ...req.body,
        userId,
        id: uuidv4()
      };
      
      const task = await agentStorage.createTask(taskData);
      
      // If agent is running, execute the task immediately
      if (taskData.agentId) {
        const agent = await agentStorage.getAgent(taskData.agentId, userId);
        if (agent?.status === 'running') {
          // Execute task in background
          agentExecutor.executeTask(task).catch(console.error);
        }
      }
      
      res.status(201).json(task);
    } catch (error) {
      console.error("Error creating task:", error);
      res.status(500).json({ message: "Failed to create task" });
    }
  });

  app.get("/api/agents/:id/logs", isAuthenticated, async (req: any, res) => {
    try {
      const { id } = req.params;
      const userId = req.user.claims.sub;
      
      const agent = await agentStorage.getAgent(id, userId);
      if (!agent) {
        return res.status(404).json({ message: "Agent not found" });
      }
      
      const logs = await agentStorage.getAgentLogs(id);
      res.json(logs);
    } catch (error) {
      console.error("Error fetching agent logs:", error);
      res.status(500).json({ message: "Failed to fetch agent logs" });
    }
  });

  const httpServer = createServer(app);
  return httpServer;
}
