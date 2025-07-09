import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Switch } from "@/components/ui/switch";
import { Progress } from "@/components/ui/progress";
import { 
  Play, 
  Pause, 
  Square, 
  Bot, 
  FileText, 
  Code, 
  Database, 
  Globe, 
  Settings,
  Plus,
  Trash2,
  Eye,
  Download,
  Upload,
  Clock,
  CheckCircle,
  XCircle,
  AlertCircle
} from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { apiRequest } from "@/lib/queryClient";

interface Agent {
  id: string;
  name: string;
  description: string;
  type: string;
  status: 'idle' | 'running' | 'paused' | 'completed' | 'error';
  progress: number;
  lastRun?: string;
  config: Record<string, any>;
  logs: AgentLog[];
}

interface AgentLog {
  id: string;
  timestamp: string;
  level: 'info' | 'warning' | 'error' | 'success';
  message: string;
  details?: string;
}

interface Task {
  id: string;
  agentId: string;
  name: string;
  description: string;
  type: string;
  status: 'pending' | 'running' | 'completed' | 'failed';
  progress: number;
  result?: any;
  error?: string;
  createdAt: string;
  completedAt?: string;
}

const agentTypes = [
  {
    id: 'code-saver',
    name: 'Code Saver',
    description: 'Saves code snippets to various file formats',
    icon: Code,
    color: 'bg-blue-500'
  },
  {
    id: 'document-manager',
    name: 'Document Manager',
    description: 'Creates and manages documents in multiple formats',
    icon: FileText,
    color: 'bg-green-500'
  },
  {
    id: 'web-scraper',
    name: 'Web Scraper',
    description: 'Extracts data from websites and APIs',
    icon: Globe,
    color: 'bg-purple-500'
  },
  {
    id: 'data-processor',
    name: 'Data Processor',
    description: 'Processes and transforms data files',
    icon: Database,
    color: 'bg-orange-500'
  },
  {
    id: 'file-organizer',
    name: 'File Organizer',
    description: 'Organizes files and folders automatically',
    icon: Settings,
    color: 'bg-indigo-500'
  }
];

const taskTemplates = [
  {
    name: 'Save Code to File',
    type: 'code-saver',
    description: 'Save code snippets to text or document files',
    fields: [
      { name: 'code', label: 'Code Content', type: 'textarea', required: true },
      { name: 'filename', label: 'File Name', type: 'text', required: true },
      { name: 'format', label: 'Format', type: 'select', options: ['txt', 'docx', 'pdf', 'md'], required: true },
      { name: 'folder', label: 'Folder Path', type: 'text', required: false }
    ]
  },
  {
    name: 'Create Documentation',
    type: 'document-manager',
    description: 'Generate project documentation',
    fields: [
      { name: 'title', label: 'Document Title', type: 'text', required: true },
      { name: 'content', label: 'Content', type: 'textarea', required: true },
      { name: 'format', label: 'Format', type: 'select', options: ['docx', 'pdf', 'md', 'html'], required: true },
      { name: 'template', label: 'Template', type: 'select', options: ['basic', 'technical', 'report'], required: false }
    ]
  },
  {
    name: 'Extract Web Data',
    type: 'web-scraper',
    description: 'Scrape data from web pages',
    fields: [
      { name: 'url', label: 'URL', type: 'text', required: true },
      { name: 'selector', label: 'CSS Selector', type: 'text', required: false },
      { name: 'output', label: 'Output Format', type: 'select', options: ['json', 'csv', 'txt'], required: true }
    ]
  },
  {
    name: 'Process Data File',
    type: 'data-processor',
    description: 'Transform and process data files',
    fields: [
      { name: 'inputFile', label: 'Input File', type: 'file', required: true },
      { name: 'operation', label: 'Operation', type: 'select', options: ['convert', 'filter', 'aggregate', 'validate'], required: true },
      { name: 'outputFormat', label: 'Output Format', type: 'select', options: ['json', 'csv', 'xml'], required: true }
    ]
  },
  {
    name: 'Organize Files',
    type: 'file-organizer',
    description: 'Automatically organize files in folders',
    fields: [
      { name: 'sourceFolder', label: 'Source Folder', type: 'text', required: true },
      { name: 'rules', label: 'Organization Rules', type: 'textarea', required: true },
      { name: 'action', label: 'Action', type: 'select', options: ['move', 'copy', 'link'], required: true }
    ]
  }
];

export default function AIAgents() {
  const [agents, setAgents] = useState<Agent[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [selectedAgent, setSelectedAgent] = useState<Agent | null>(null);
  const [activeTab, setActiveTab] = useState('agents');
  const [newAgentType, setNewAgentType] = useState('');
  const [newTaskData, setNewTaskData] = useState<Record<string, any>>({});
  const [showCreateAgent, setShowCreateAgent] = useState(false);
  const [showCreateTask, setShowCreateTask] = useState(false);
  const { toast } = useToast();
  const queryClient = useQueryClient();

  // Fetch agents
  const { data: agentsData, isLoading: agentsLoading } = useQuery({
    queryKey: ['/api/agents'],
    refetchInterval: 5000 // Refresh every 5 seconds
  });

  // Fetch tasks
  const { data: tasksData, isLoading: tasksLoading } = useQuery({
    queryKey: ['/api/tasks'],
    refetchInterval: 2000 // Refresh every 2 seconds
  });

  // Create agent mutation
  const createAgentMutation = useMutation({
    mutationFn: async (agentData: any) => {
      return apiRequest('/api/agents', {
        method: 'POST',
        body: JSON.stringify(agentData)
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['/api/agents'] });
      setShowCreateAgent(false);
      toast({
        title: "Agent Created",
        description: "AI agent created successfully!",
      });
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: "Failed to create agent",
        variant: "destructive",
      });
    }
  });

  // Create task mutation
  const createTaskMutation = useMutation({
    mutationFn: async (taskData: any) => {
      return apiRequest('/api/tasks', {
        method: 'POST',
        body: JSON.stringify(taskData)
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['/api/tasks'] });
      setShowCreateTask(false);
      setNewTaskData({});
      toast({
        title: "Task Created",
        description: "Task assigned to agent successfully!",
      });
    },
    onError: (error) => {
      toast({
        title: "Error",
        description: "Failed to create task",
        variant: "destructive",
      });
    }
  });

  // Control agent mutations
  const controlAgentMutation = useMutation({
    mutationFn: async ({ agentId, action }: { agentId: string; action: string }) => {
      return apiRequest(`/api/agents/${agentId}/${action}`, {
        method: 'POST'
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['/api/agents'] });
    }
  });

  useEffect(() => {
    if (agentsData) {
      setAgents(agentsData);
    }
  }, [agentsData]);

  useEffect(() => {
    if (tasksData) {
      setTasks(tasksData);
    }
  }, [tasksData]);

  const handleCreateAgent = (agentData: any) => {
    createAgentMutation.mutate(agentData);
  };

  const handleCreateTask = (taskData: any) => {
    createTaskMutation.mutate(taskData);
  };

  const handleControlAgent = (agentId: string, action: string) => {
    controlAgentMutation.mutate({ agentId, action });
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'running':
        return <Play className="w-4 h-4 text-green-500" />;
      case 'paused':
        return <Pause className="w-4 h-4 text-yellow-500" />;
      case 'completed':
        return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'error':
        return <XCircle className="w-4 h-4 text-red-500" />;
      default:
        return <Square className="w-4 h-4 text-gray-500" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'running':
        return 'bg-green-100 text-green-800';
      case 'paused':
        return 'bg-yellow-100 text-yellow-800';
      case 'completed':
        return 'bg-green-100 text-green-800';
      case 'error':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">AI Agents</h1>
          <p className="text-lg text-gray-600">Create and manage AI agents to automate your tasks</p>
        </div>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="agents">Agents</TabsTrigger>
            <TabsTrigger value="tasks">Tasks</TabsTrigger>
            <TabsTrigger value="templates">Templates</TabsTrigger>
          </TabsList>

          <TabsContent value="agents" className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-semibold text-gray-900">Active Agents</h2>
              <Button onClick={() => setShowCreateAgent(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Create Agent
              </Button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {agents.map((agent) => {
                const agentType = agentTypes.find(t => t.id === agent.type);
                const IconComponent = agentType?.icon || Bot;
                
                return (
                  <Card key={agent.id} className="hover:shadow-lg transition-shadow">
                    <CardHeader className="pb-3">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <div className={`p-2 rounded-lg ${agentType?.color || 'bg-gray-500'}`}>
                            <IconComponent className="w-5 h-5 text-white" />
                          </div>
                          <div>
                            <CardTitle className="text-lg">{agent.name}</CardTitle>
                            <CardDescription>{agent.description}</CardDescription>
                          </div>
                        </div>
                        <Badge className={getStatusColor(agent.status)}>
                          {getStatusIcon(agent.status)}
                          <span className="ml-1">{agent.status}</span>
                        </Badge>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        {agent.progress > 0 && (
                          <div>
                            <div className="flex justify-between text-sm mb-1">
                              <span>Progress</span>
                              <span>{agent.progress}%</span>
                            </div>
                            <Progress value={agent.progress} className="h-2" />
                          </div>
                        )}
                        
                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleControlAgent(agent.id, 'start')}
                            disabled={agent.status === 'running'}
                          >
                            <Play className="w-4 h-4" />
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleControlAgent(agent.id, 'pause')}
                            disabled={agent.status !== 'running'}
                          >
                            <Pause className="w-4 h-4" />
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleControlAgent(agent.id, 'stop')}
                            disabled={agent.status === 'idle'}
                          >
                            <Square className="w-4 h-4" />
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => setSelectedAgent(agent)}
                          >
                            <Eye className="w-4 h-4" />
                          </Button>
                        </div>
                        
                        {agent.lastRun && (
                          <div className="text-sm text-gray-500 flex items-center gap-1">
                            <Clock className="w-4 h-4" />
                            Last run: {new Date(agent.lastRun).toLocaleString()}
                          </div>
                        )}
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>

            {showCreateAgent && (
              <Card>
                <CardHeader>
                  <CardTitle>Create New Agent</CardTitle>
                  <CardDescription>Configure a new AI agent to automate your tasks</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="agent-name">Agent Name</Label>
                      <Input
                        id="agent-name"
                        placeholder="Enter agent name"
                        value={newTaskData.name || ''}
                        onChange={(e) => setNewTaskData(prev => ({ ...prev, name: e.target.value }))}
                      />
                    </div>
                    <div>
                      <Label htmlFor="agent-type">Agent Type</Label>
                      <Select value={newAgentType} onValueChange={setNewAgentType}>
                        <SelectTrigger>
                          <SelectValue placeholder="Select agent type" />
                        </SelectTrigger>
                        <SelectContent>
                          {agentTypes.map(type => (
                            <SelectItem key={type.id} value={type.id}>
                              {type.name}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                  
                  <div>
                    <Label htmlFor="agent-description">Description</Label>
                    <Textarea
                      id="agent-description"
                      placeholder="Describe what this agent will do"
                      value={newTaskData.description || ''}
                      onChange={(e) => setNewTaskData(prev => ({ ...prev, description: e.target.value }))}
                    />
                  </div>
                  
                  <div className="flex gap-2">
                    <Button
                      onClick={() => handleCreateAgent({ ...newTaskData, type: newAgentType })}
                      disabled={!newTaskData.name || !newAgentType}
                    >
                      Create Agent
                    </Button>
                    <Button
                      variant="outline"
                      onClick={() => setShowCreateAgent(false)}
                    >
                      Cancel
                    </Button>
                  </div>
                </CardContent>
              </Card>
            )}
          </TabsContent>

          <TabsContent value="tasks" className="space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-semibold text-gray-900">Task Queue</h2>
              <Button onClick={() => setShowCreateTask(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Create Task
              </Button>
            </div>

            <div className="space-y-4">
              {tasks.map((task) => (
                <Card key={task.id}>
                  <CardContent className="p-4">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <div className="flex items-center gap-2">
                          {getStatusIcon(task.status)}
                          <div>
                            <h3 className="font-medium">{task.name}</h3>
                            <p className="text-sm text-gray-600">{task.description}</p>
                          </div>
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <Badge className={getStatusColor(task.status)}>
                          {task.status}
                        </Badge>
                        <div className="text-sm text-gray-500">
                          {new Date(task.createdAt).toLocaleString()}
                        </div>
                      </div>
                    </div>
                    {task.progress > 0 && (
                      <div className="mt-4">
                        <Progress value={task.progress} className="h-2" />
                      </div>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="templates" className="space-y-6">
            <div className="text-center mb-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-2">Task Templates</h2>
              <p className="text-gray-600">Pre-configured task templates for common operations</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {taskTemplates.map((template, index) => {
                const agentType = agentTypes.find(t => t.id === template.type);
                const IconComponent = agentType?.icon || Bot;
                
                return (
                  <Card key={index} className="hover:shadow-lg transition-shadow">
                    <CardHeader>
                      <div className="flex items-center gap-3">
                        <div className={`p-2 rounded-lg ${agentType?.color || 'bg-gray-500'}`}>
                          <IconComponent className="w-5 h-5 text-white" />
                        </div>
                        <div>
                          <CardTitle className="text-lg">{template.name}</CardTitle>
                          <CardDescription>{template.description}</CardDescription>
                        </div>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-3">
                        <div className="text-sm text-gray-600">
                          <strong>Required Fields:</strong>
                        </div>
                        <div className="grid grid-cols-1 gap-2">
                          {template.fields.map((field, fieldIndex) => (
                            <div key={fieldIndex} className="flex items-center gap-2 text-sm">
                              <span className="text-gray-600">{field.label}</span>
                              <Badge variant={field.required ? "default" : "secondary"}>
                                {field.type}
                              </Badge>
                              {field.required && (
                                <span className="text-red-500 text-xs">*</span>
                              )}
                            </div>
                          ))}
                        </div>
                        <Button
                          className="w-full"
                          onClick={() => {
                            setNewTaskData({ template: template.name, type: template.type });
                            setShowCreateTask(true);
                          }}
                        >
                          Use Template
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}