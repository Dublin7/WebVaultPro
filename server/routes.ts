import type { Express } from "express";
import { createServer, type Server } from "http";
import { storage } from "./storage";
import { setupAuth, isAuthenticated } from "./replitAuth";
import { agentStorage, agentExecutor } from "./agents";
import { insertAgentSchema, insertTaskSchema } from "@shared/schema";
import { v4 as uuidv4 } from "uuid";
import { promises as fs } from "fs";
import path from "path";

// Helper functions for synthetic media generation
function generateSyntheticAudio(prompt: string, genre: string, duration: number): Buffer {
  // Create a simple synthetic audio file (WAV format header + data)
  const sampleRate = 44100;
  const channels = 2;
  const bitsPerSample = 16;
  const dataSize = sampleRate * channels * (bitsPerSample / 8) * duration;
  
  // WAV header (44 bytes)
  const header = Buffer.alloc(44);
  header.write('RIFF', 0);
  header.writeUInt32LE(dataSize + 36, 4);
  header.write('WAVE', 8);
  header.write('fmt ', 12);
  header.writeUInt32LE(16, 16);
  header.writeUInt16LE(1, 20);
  header.writeUInt16LE(channels, 22);
  header.writeUInt32LE(sampleRate, 24);
  header.writeUInt32LE(sampleRate * channels * (bitsPerSample / 8), 28);
  header.writeUInt16LE(channels * (bitsPerSample / 8), 32);
  header.writeUInt16LE(bitsPerSample, 34);
  header.write('data', 36);
  header.writeUInt32LE(dataSize, 40);
  
  // Generate synthetic audio data based on genre
  const audioData = Buffer.alloc(dataSize);
  let frequency = 440; // Default frequency
  
  switch (genre) {
    case 'techno':
      frequency = 130; // Lower frequency for techno bass
      break;
    case 'drum-bass':
      frequency = 90; // Even lower for drum & bass
      break;
    case 'house':
      frequency = 120;
      break;
    case 'trance':
      frequency = 140;
      break;
    default:
      frequency = 440;
  }
  
  // Generate simple sine wave
  for (let i = 0; i < dataSize; i += 2) {
    const sample = Math.sin(2 * Math.PI * frequency * i / (sampleRate * 2)) * 0.3;
    const intSample = Math.round(sample * 32767);
    audioData.writeInt16LE(intSample, i);
  }
  
  return Buffer.concat([header, audioData]);
}

function generateSyntheticVideo(prompt: string, style: string, duration: number): Buffer {
  // Create a simple synthetic video file placeholder
  // In a real implementation, this would use AI video generation
  const videoHeader = Buffer.from([
    0x00, 0x00, 0x00, 0x20, 0x66, 0x74, 0x79, 0x70, // MP4 header
    0x69, 0x73, 0x6F, 0x6D, 0x00, 0x00, 0x02, 0x00,
    0x69, 0x73, 0x6F, 0x6D, 0x69, 0x73, 0x6F, 0x32,
    0x61, 0x76, 0x63, 0x31, 0x6D, 0x70, 0x34, 0x31
  ]);
  
  // Generate synthetic video data based on style
  const frameSize = 1920 * 1080 * 3; // RGB24 format
  const framesPerSecond = 30;
  const totalFrames = duration * framesPerSecond;
  const videoDataSize = frameSize * totalFrames;
  
  const videoData = Buffer.alloc(videoDataSize);
  
  // Fill with different colors based on style
  let fillColor = { r: 128, g: 128, b: 128 }; // Default gray
  
  switch (style) {
    case 'realistic':
      fillColor = { r: 100, g: 150, b: 200 }; // Blue tones
      break;
    case 'animated':
      fillColor = { r: 255, g: 200, b: 100 }; // Warm colors
      break;
    case 'abstract':
      fillColor = { r: 200, g: 100, b: 255 }; // Purple tones
      break;
    case 'cinematic':
      fillColor = { r: 50, g: 50, b: 100 }; // Dark blue
      break;
    case 'tech':
      fillColor = { r: 0, g: 255, b: 150 }; // Cyan/green
      break;
  }
  
  // Fill video data with the style color
  for (let i = 0; i < videoDataSize; i += 3) {
    videoData[i] = fillColor.r;
    videoData[i + 1] = fillColor.g;
    videoData[i + 2] = fillColor.b;
  }
  
  return Buffer.concat([videoHeader, videoData]);
}

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

  // AI Studio routes
  app.post("/api/ai-studio/generate-audio", isAuthenticated, async (req: any, res) => {
    try {
      const { prompt, genre, duration } = req.body;
      const userId = req.user.claims.sub;
      
      // Create audio generation folder
      const audioFolder = './generated-audio';
      await fs.mkdir(audioFolder, { recursive: true });
      
      // Simulate audio generation process
      const taskId = uuidv4();
      const filename = `${genre}_${Date.now()}.wav`;
      const filePath = path.join(audioFolder, filename);
      
      // Create a simple audio file placeholder (in real implementation, this would use AI)
      const audioData = generateSyntheticAudio(prompt, genre, duration);
      await fs.writeFile(filePath, audioData);
      
      const result = {
        id: taskId,
        type: 'audio',
        prompt: prompt,
        genre: genre,
        duration: duration,
        status: 'completed',
        progress: 100,
        result: {
          filename: filename,
          filePath: filePath,
          size: audioData.length,
          format: 'wav',
          genre: genre,
          duration: duration
        },
        createdAt: new Date().toISOString()
      };
      
      res.json(result);
    } catch (error) {
      console.error("Error generating audio:", error);
      res.status(500).json({ message: "Failed to generate audio" });
    }
  });

  app.post("/api/ai-studio/generate-video", isAuthenticated, async (req: any, res) => {
    try {
      const { prompt, style, duration } = req.body;
      const userId = req.user.claims.sub;
      
      // Create video generation folder
      const videoFolder = './generated-videos';
      await fs.mkdir(videoFolder, { recursive: true });
      
      // Simulate video generation process
      const taskId = uuidv4();
      const filename = `${style}_${Date.now()}.mp4`;
      const filePath = path.join(videoFolder, filename);
      
      // Create a simple video file placeholder (in real implementation, this would use AI)
      const videoData = generateSyntheticVideo(prompt, style, duration);
      await fs.writeFile(filePath, videoData);
      
      const result = {
        id: taskId,
        type: 'video',
        prompt: prompt,
        style: style,
        duration: duration,
        status: 'completed',
        progress: 100,
        result: {
          filename: filename,
          filePath: filePath,
          size: videoData.length,
          format: 'mp4',
          style: style,
          duration: duration,
          resolution: '1920x1080'
        },
        createdAt: new Date().toISOString()
      };
      
      res.json(result);
    } catch (error) {
      console.error("Error generating video:", error);
      res.status(500).json({ message: "Failed to generate video" });
    }
  });

  // Integration endpoints
  app.post("/api/integrations/galxe/connect", isAuthenticated, async (req: any, res) => {
    try {
      const { address, signature } = req.body;
      const userId = req.user.claims.sub;
      
      // Simulate Galxe connection
      const result = {
        success: true,
        address: address,
        credentials: ['Developer', 'Early Adopter', 'Community Member'],
        campaigns: 5,
        points: 1250
      };
      
      res.json(result);
    } catch (error) {
      console.error("Error connecting to Galxe:", error);
      res.status(500).json({ message: "Failed to connect to Galxe" });
    }
  });

  app.post("/api/integrations/metaschool/sync", isAuthenticated, async (req: any, res) => {
    try {
      const { courseId } = req.body;
      const userId = req.user.claims.sub;
      
      // Simulate Metaschool sync
      const result = {
        success: true,
        courses: [
          { id: 'web3-basics', name: 'Web3 Basics', progress: 75, certificate: true },
          { id: 'smart-contracts', name: 'Smart Contracts', progress: 45, certificate: false },
          { id: 'defi-development', name: 'DeFi Development', progress: 20, certificate: false }
        ],
        totalCertificates: 1,
        totalProgress: 46.7
      };
      
      res.json(result);
    } catch (error) {
      console.error("Error syncing with Metaschool:", error);
      res.status(500).json({ message: "Failed to sync with Metaschool" });
    }
  });

  app.post("/api/integrations/google-keep/save", isAuthenticated, async (req: any, res) => {
    try {
      const { title, content, labels } = req.body;
      const userId = req.user.claims.sub;
      
      // Simulate Google Keep save
      const result = {
        success: true,
        noteId: uuidv4(),
        title: title,
        content: content,
        labels: labels || [],
        createdAt: new Date().toISOString(),
        url: `https://keep.google.com/u/0/#NOTE/${uuidv4()}`
      };
      
      res.json(result);
    } catch (error) {
      console.error("Error saving to Google Keep:", error);
      res.status(500).json({ message: "Failed to save to Google Keep" });
    }
  });

  const httpServer = createServer(app);
  return httpServer;
}
