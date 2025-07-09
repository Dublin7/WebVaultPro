import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Copy, Terminal, Zap, FileText, Database, Network, Settings } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

interface CommandTemplate {
  name: string;
  description: string;
  template: string;
  category: string;
  icon: React.ElementType;
  variables: string[];
}

const commandTemplates: CommandTemplate[] = [
  {
    name: "File Operations",
    description: "Find files and directories with specific patterns",
    template: "find {{directory}} -name '{{pattern}}' -type {{type}}",
    category: "files",
    icon: FileText,
    variables: ["directory", "pattern", "type"]
  },
  {
    name: "Process Management",
    description: "Kill processes by name or port",
    template: "ps aux | grep {{process_name}} | grep -v grep | awk '{print $2}' | xargs kill -{{signal}}",
    category: "system",
    icon: Terminal,
    variables: ["process_name", "signal"]
  },
  {
    name: "Port Check",
    description: "Check what's running on a specific port",
    template: "lsof -i :{{port}} || ss -tlnp | grep :{{port}}",
    category: "network",
    icon: Network,
    variables: ["port"]
  },
  {
    name: "Database Backup",
    description: "Create PostgreSQL database backup",
    template: "pg_dump -h {{host}} -U {{username}} -d {{database}} > {{backup_file}}.sql",
    category: "database",
    icon: Database,
    variables: ["host", "username", "database", "backup_file"]
  },
  {
    name: "System Monitoring",
    description: "Monitor specific process resources",
    template: "top -p $(pgrep {{process_name}} | tr '\\n' ',')",
    category: "system",
    icon: Settings,
    variables: ["process_name"]
  },
  {
    name: "Git Log Search",
    description: "Search Git history with filters",
    template: "git log --oneline --graph --all --since='{{since}}' --author='{{author}}'",
    category: "git",
    icon: Terminal,
    variables: ["since", "author"]
  },
  {
    name: "Docker Exec",
    description: "Execute commands in Docker containers",
    template: "docker exec -it {{container_name}} {{command}}",
    category: "docker",
    icon: Terminal,
    variables: ["container_name", "command"]
  },
  {
    name: "Log Monitoring",
    description: "Monitor log files with timestamps",
    template: "tail -f {{log_file}} | grep {{pattern}} | while read line; do echo \"$(date): $line\"; done",
    category: "logs",
    icon: FileText,
    variables: ["log_file", "pattern"]
  },
  {
    name: "File Permissions",
    description: "Change file permissions recursively",
    template: "find {{directory}} -type {{type}} -exec chmod {{permissions}} {} +",
    category: "files",
    icon: FileText,
    variables: ["directory", "type", "permissions"]
  },
  {
    name: "Disk Space Analysis",
    description: "Find largest files/directories",
    template: "du -h {{directory}} | sort -hr | head -{{count}}",
    category: "system",
    icon: Settings,
    variables: ["directory", "count"]
  },
  {
    name: "Network Connectivity",
    description: "Test network connectivity and response time",
    template: "ping -c {{count}} {{host}} && curl -o /dev/null -s -w 'Response time: %{time_total}s\\n' {{url}}",
    category: "network",
    icon: Network,
    variables: ["count", "host", "url"]
  },
  {
    name: "Git Branch Management",
    description: "Clean up old Git branches",
    template: "git branch --merged | grep -v '\\*\\|main\\|master' | xargs -n 1 git branch -d",
    category: "git",
    icon: Terminal,
    variables: []
  }
];

const quickCommands = [
  { name: "List all processes", command: "ps aux", description: "Show all running processes with details" },
  { name: "Check disk usage", command: "df -h", description: "Display filesystem disk space usage" },
  { name: "Check memory usage", command: "free -h", description: "Show memory usage in human-readable format" },
  { name: "Current directory size", command: "du -sh .", description: "Show size of current directory" },
  { name: "Find large files", command: "find . -type f -size +100M", description: "Find files larger than 100MB" },
  { name: "Check open ports", command: "ss -tuln", description: "List all listening ports" },
  { name: "System uptime", command: "uptime", description: "Show system uptime and load" },
  { name: "Environment variables", command: "env | sort", description: "List all environment variables" },
  { name: "Kill Node processes", command: "pkill -f node", description: "Kill all Node.js processes" },
  { name: "Check CPU info", command: "lscpu", description: "Display CPU information" },
  { name: "Monitor network traffic", command: "nethogs", description: "Show network usage by process" },
  { name: "List recent files", command: "ls -lt | head -20", description: "Show 20 most recently modified files" }
];

const exampleCommands = [
  {
    title: "Development Server Management",
    description: "Common commands for managing development servers",
    examples: [
      {
        task: "Kill all Node.js processes running on port 3000",
        command: "lsof -ti:3000 | xargs kill -9",
        explanation: "First finds process IDs using port 3000, then kills them"
      },
      {
        task: "Start a development server in background",
        command: "nohup npm run dev > server.log 2>&1 &",
        explanation: "Runs npm dev command in background, redirecting output to server.log"
      },
      {
        task: "Check if a port is available",
        command: "nc -z localhost 8080 && echo 'Port in use' || echo 'Port available'",
        explanation: "Uses netcat to check if port 8080 is open"
      }
    ]
  },
  {
    title: "File Management & Search",
    description: "Efficient file operations and searches",
    examples: [
      {
        task: "Find all JavaScript files modified in last 7 days",
        command: "find . -name '*.js' -type f -mtime -7",
        explanation: "Searches for .js files modified within the last 7 days"
      },
      {
        task: "Find and delete empty directories",
        command: "find . -type d -empty -delete",
        explanation: "Finds all empty directories and deletes them"
      },
      {
        task: "Search for text in all files",
        command: "grep -r 'TODO' . --include='*.js' --include='*.ts'",
        explanation: "Recursively searches for 'TODO' in JavaScript and TypeScript files"
      },
      {
        task: "Copy files with progress bar",
        command: "rsync -av --progress source/ destination/",
        explanation: "Copies files with progress indication using rsync"
      }
    ]
  },
  {
    title: "Git Operations",
    description: "Advanced Git commands for daily development",
    examples: [
      {
        task: "Undo last commit but keep changes",
        command: "git reset --soft HEAD~1",
        explanation: "Resets to previous commit but keeps changes staged"
      },
      {
        task: "Find commits that changed a specific file",
        command: "git log --follow --patch -- filename.js",
        explanation: "Shows all commits that modified filename.js with diffs"
      },
      {
        task: "Create and switch to new branch",
        command: "git checkout -b feature/new-feature",
        explanation: "Creates a new branch and switches to it in one command"
      },
      {
        task: "Show files changed in last commit",
        command: "git diff --name-only HEAD~1 HEAD",
        explanation: "Lists files that were changed in the last commit"
      }
    ]
  },
  {
    title: "System Monitoring",
    description: "Commands to monitor system performance",
    examples: [
      {
        task: "Monitor disk I/O in real-time",
        command: "iostat -x 1",
        explanation: "Shows disk I/O statistics updated every second"
      },
      {
        task: "Find processes using most memory",
        command: "ps aux --sort=-%mem | head -10",
        explanation: "Lists top 10 processes by memory usage"
      },
      {
        task: "Monitor log file changes",
        command: "tail -f /var/log/syslog | grep -i error",
        explanation: "Follows syslog and filters for error messages"
      },
      {
        task: "Check network connections",
        command: "netstat -tuln | grep LISTEN",
        explanation: "Shows all listening network connections"
      }
    ]
  }
];

export default function ShellGenerator() {
  const [selectedTemplate, setSelectedTemplate] = useState<CommandTemplate | null>(null);
  const [variables, setVariables] = useState<Record<string, string>>({});
  const [generatedCommand, setGeneratedCommand] = useState("");
  const [customPrompt, setCustomPrompt] = useState("");
  const [filterCategory, setFilterCategory] = useState("all");
  const { toast } = useToast();

  const categories = ["all", "files", "system", "network", "database", "git", "docker", "logs"];

  const filteredTemplates = filterCategory === "all" 
    ? commandTemplates 
    : commandTemplates.filter(t => t.category === filterCategory);

  const handleTemplateSelect = (template: CommandTemplate) => {
    setSelectedTemplate(template);
    setVariables({});
    setGeneratedCommand("");
  };

  const handleVariableChange = (variable: string, value: string) => {
    setVariables(prev => ({
      ...prev,
      [variable]: value
    }));
  };

  const generateCommand = () => {
    if (!selectedTemplate) return;

    let command = selectedTemplate.template;
    selectedTemplate.variables.forEach(variable => {
      const value = variables[variable] || `{{${variable}}}`;
      command = command.replace(new RegExp(`{{${variable}}}`, 'g'), value);
    });

    setGeneratedCommand(command);
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    toast({
      title: "Copied to clipboard",
      description: "Command copied successfully!",
    });
  };

  const generateFromPrompt = () => {
    // This would typically call an AI service to generate commands
    // For now, we'll provide some basic pattern matching
    const prompt = customPrompt.toLowerCase();
    let suggestedCommand = "";

    if (prompt.includes("find") && prompt.includes("file")) {
      suggestedCommand = "find . -name '*.txt' -type f";
    } else if (prompt.includes("kill") && prompt.includes("process")) {
      suggestedCommand = "pkill -f process_name";
    } else if (prompt.includes("port") && prompt.includes("check")) {
      suggestedCommand = "ss -tlnp | grep :8080";
    } else if (prompt.includes("backup") && prompt.includes("database")) {
      suggestedCommand = "pg_dump -h localhost -U username database_name > backup.sql";
    } else if (prompt.includes("git") && prompt.includes("log")) {
      suggestedCommand = "git log --oneline --graph --all";
    } else if (prompt.includes("docker") && prompt.includes("container")) {
      suggestedCommand = "docker ps -a";
    } else {
      suggestedCommand = "# Please provide a more specific description";
    }

    setGeneratedCommand(suggestedCommand);
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Shell Command Generator</h1>
          <p className="text-lg text-gray-600">Generate powerful shell commands from templates or natural language</p>
        </div>

        <Tabs defaultValue="templates" className="w-full">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="templates">Templates</TabsTrigger>
            <TabsTrigger value="examples">Examples</TabsTrigger>
            <TabsTrigger value="prompt">AI Prompt</TabsTrigger>
            <TabsTrigger value="quick">Quick Commands</TabsTrigger>
          </TabsList>

          <TabsContent value="templates" className="space-y-6">
            <div className="flex items-center gap-4 mb-6">
              <Label htmlFor="category">Filter by category:</Label>
              <Select value={filterCategory} onValueChange={setFilterCategory}>
                <SelectTrigger className="w-48">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {categories.map(cat => (
                    <SelectItem key={cat} value={cat}>
                      {cat.charAt(0).toUpperCase() + cat.slice(1)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {filteredTemplates.map((template, index) => {
                const IconComponent = template.icon;
                return (
                  <Card 
                    key={index} 
                    className={`cursor-pointer transition-all hover:shadow-md ${
                      selectedTemplate === template ? 'ring-2 ring-blue-500' : ''
                    }`}
                    onClick={() => handleTemplateSelect(template)}
                  >
                    <CardHeader className="pb-3">
                      <div className="flex items-center gap-2">
                        <IconComponent className="w-5 h-5 text-blue-600" />
                        <CardTitle className="text-lg">{template.name}</CardTitle>
                      </div>
                      <CardDescription>{template.description}</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <Badge variant="outline" className="mb-2">
                        {template.category}
                      </Badge>
                      <code className="text-sm bg-gray-100 p-2 rounded block">
                        {template.template}
                      </code>
                    </CardContent>
                  </Card>
                );
              })}
            </div>

            {selectedTemplate && (
              <Card>
                <CardHeader>
                  <CardTitle>Configure Template: {selectedTemplate.name}</CardTitle>
                  <CardDescription>Fill in the variables to generate your command</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {selectedTemplate.variables.map(variable => (
                      <div key={variable}>
                        <Label htmlFor={variable}>{variable.replace('_', ' ')}</Label>
                        <Input
                          id={variable}
                          placeholder={`Enter ${variable}`}
                          value={variables[variable] || ''}
                          onChange={(e) => handleVariableChange(variable, e.target.value)}
                        />
                      </div>
                    ))}
                  </div>
                  <Button onClick={generateCommand} className="w-full">
                    <Zap className="w-4 h-4 mr-2" />
                    Generate Command
                  </Button>
                </CardContent>
              </Card>
            )}
          </TabsContent>

          <TabsContent value="examples" className="space-y-6">
            {exampleCommands.map((section, sectionIndex) => (
              <Card key={sectionIndex}>
                <CardHeader>
                  <CardTitle className="text-xl">{section.title}</CardTitle>
                  <CardDescription>{section.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-6">
                    {section.examples.map((example, index) => (
                      <div key={index} className="border-l-4 border-blue-500 pl-4 py-2">
                        <h4 className="font-medium text-gray-900 mb-2">{example.task}</h4>
                        <div className="bg-gray-900 text-green-400 p-3 rounded-lg font-mono text-sm mb-2">
                          <div className="flex items-center justify-between">
                            <span>$ {example.command}</span>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => copyToClipboard(example.command)}
                              className="text-green-400 hover:text-green-300"
                            >
                              <Copy className="w-4 h-4" />
                            </Button>
                          </div>
                        </div>
                        <p className="text-sm text-gray-600 italic">{example.explanation}</p>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </TabsContent>

          <TabsContent value="prompt" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Natural Language Command Generation</CardTitle>
                <CardDescription>Describe what you want to do and we'll generate the command</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <Label htmlFor="prompt">Describe your task:</Label>
                  <Textarea
                    id="prompt"
                    placeholder="e.g., 'Find all JavaScript files modified in the last 7 days' or 'Kill all Node.js processes'"
                    value={customPrompt}
                    onChange={(e) => setCustomPrompt(e.target.value)}
                    className="min-h-24"
                  />
                </div>
                <Button onClick={generateFromPrompt} disabled={!customPrompt.trim()}>
                  <Terminal className="w-4 h-4 mr-2" />
                  Generate from Description
                </Button>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="quick" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {quickCommands.map((cmd, index) => (
                <Card key={index} className="cursor-pointer hover:shadow-md transition-shadow">
                  <CardContent className="p-4">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <h3 className="font-medium text-gray-900 mb-1">{cmd.name}</h3>
                        <p className="text-sm text-gray-600 mb-2">{cmd.description}</p>
                        <div className="bg-gray-900 text-green-400 p-2 rounded font-mono text-sm">
                          <code>{cmd.command}</code>
                        </div>
                      </div>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => copyToClipboard(cmd.command)}
                        className="ml-2 flex-shrink-0"
                      >
                        <Copy className="w-4 h-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </TabsContent>
        </Tabs>

        {generatedCommand && (
          <Card className="mt-6">
            <CardHeader>
              <CardTitle>Generated Command</CardTitle>
              <CardDescription>Your command is ready to use</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="bg-gray-900 text-green-400 p-4 rounded-lg font-mono text-sm">
                <div className="flex items-center justify-between">
                  <span>$ {generatedCommand}</span>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => copyToClipboard(generatedCommand)}
                    className="text-green-400 hover:text-green-300"
                  >
                    <Copy className="w-4 h-4" />
                  </Button>
                </div>
              </div>
              <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-md">
                <p className="text-sm text-yellow-800">
                  <strong>Warning:</strong> Always review generated commands before executing them. 
                  Test in a safe environment first.
                </p>
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}