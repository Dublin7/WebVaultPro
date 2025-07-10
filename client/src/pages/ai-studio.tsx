import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Progress } from "@/components/ui/progress";
import { 
  Music, 
  Video, 
  Play, 
  Pause, 
  Download, 
  Upload,
  Sparkles,
  AudioWaveform,
  Film,
  Settings,
  Volume2,
  Headphones,
  Mic,
  Camera,
  FileAudio,
  FileVideo,
  Zap,
  Globe,
  BookOpen,
  StickyNote
} from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { useMutation } from "@tanstack/react-query";
import { apiRequest } from "@/lib/queryClient";

interface GenerationTask {
  id: string;
  type: 'audio' | 'video';
  prompt: string;
  status: 'pending' | 'generating' | 'completed' | 'failed';
  progress: number;
  result?: string;
  error?: string;
  createdAt: string;
  duration?: number;
  genre?: string;
  style?: string;
}

const musicGenres = [
  { id: 'techno', name: 'Techno', description: 'Electronic dance music with repetitive beats' },
  { id: 'drum-bass', name: 'Drum & Bass', description: 'Fast breakbeats with heavy bass' },
  { id: 'house', name: 'House', description: 'Four-on-the-floor rhythm electronic music' },
  { id: 'ambient', name: 'Ambient', description: 'Atmospheric and spacey sounds' },
  { id: 'trance', name: 'Trance', description: 'Hypnotic and euphoric electronic music' },
  { id: 'dubstep', name: 'Dubstep', description: 'Heavy bass drops and wobbly synths' }
];

const videoStyles = [
  { id: 'realistic', name: 'Realistic', description: 'Photorealistic video generation' },
  { id: 'animated', name: 'Animated', description: 'Cartoon-style animation' },
  { id: 'abstract', name: 'Abstract', description: 'Abstract and artistic visuals' },
  { id: 'cinematic', name: 'Cinematic', description: 'Movie-quality cinematography' },
  { id: 'tech', name: 'Tech', description: 'Futuristic and technological theme' }
];

const integrations = [
  {
    id: 'galxe',
    name: 'Galxe',
    description: 'Web3 credential infrastructure',
    icon: Globe,
    color: 'bg-purple-500',
    features: ['NFT Campaigns', 'Credential Verification', 'Community Building']
  },
  {
    id: 'metaschool',
    name: 'Metaschool',
    description: 'Learn to build in web3',
    icon: BookOpen,
    color: 'bg-blue-500',
    features: ['Web3 Courses', 'Build Projects', 'Earn Certificates']
  },
  {
    id: 'google-keep',
    name: 'Google Keep',
    description: 'Note-taking and organization',
    icon: StickyNote,
    color: 'bg-yellow-500',
    features: ['Save Notes', 'Create Lists', 'Set Reminders']
  }
];

export default function AIStudio() {
  const [activeTab, setActiveTab] = useState('audio');
  const [audioPrompt, setAudioPrompt] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('');
  const [audioDuration, setAudioDuration] = useState(30);
  const [videoPrompt, setVideoPrompt] = useState('');
  const [selectedStyle, setSelectedStyle] = useState('');
  const [videoDuration, setVideoDuration] = useState(10);
  const [generationTasks, setGenerationTasks] = useState<GenerationTask[]>([]);
  const [isGenerating, setIsGenerating] = useState(false);
  const { toast } = useToast();

  // Audio generation mutation
  const generateAudioMutation = useMutation({
    mutationFn: async (data: any) => {
      return apiRequest('/api/ai-studio/generate-audio', {
        method: 'POST',
        body: JSON.stringify(data)
      });
    },
    onSuccess: (data) => {
      setIsGenerating(false);
      toast({
        title: "Audio Generated",
        description: "Your music has been generated successfully!",
      });
      // Add to tasks list
      setGenerationTasks(prev => [...prev, data]);
    },
    onError: (error) => {
      setIsGenerating(false);
      toast({
        title: "Generation Failed",
        description: "Failed to generate audio. Please try again.",
        variant: "destructive",
      });
    }
  });

  // Video generation mutation
  const generateVideoMutation = useMutation({
    mutationFn: async (data: any) => {
      return apiRequest('/api/ai-studio/generate-video', {
        method: 'POST',
        body: JSON.stringify(data)
      });
    },
    onSuccess: (data) => {
      setIsGenerating(false);
      toast({
        title: "Video Generated",
        description: "Your video has been generated successfully!",
      });
      // Add to tasks list
      setGenerationTasks(prev => [...prev, data]);
    },
    onError: (error) => {
      setIsGenerating(false);
      toast({
        title: "Generation Failed",
        description: "Failed to generate video. Please try again.",
        variant: "destructive",
      });
    }
  });

  // Integration actions
  const handleIntegrationAction = (integrationId: string, action: string) => {
    switch (integrationId) {
      case 'galxe':
        window.open('https://galxe.com', '_blank');
        break;
      case 'metaschool':
        window.open('https://metaschool.so', '_blank');
        break;
      case 'google-keep':
        window.open('https://keep.google.com', '_blank');
        break;
    }
  };

  const handleGenerateAudio = () => {
    if (!audioPrompt || !selectedGenre) {
      toast({
        title: "Missing Information",
        description: "Please provide a prompt and select a genre.",
        variant: "destructive",
      });
      return;
    }

    setIsGenerating(true);
    generateAudioMutation.mutate({
      prompt: audioPrompt,
      genre: selectedGenre,
      duration: audioDuration
    });
  };

  const handleGenerateVideo = () => {
    if (!videoPrompt || !selectedStyle) {
      toast({
        title: "Missing Information",
        description: "Please provide a prompt and select a style.",
        variant: "destructive",
      });
      return;
    }

    setIsGenerating(true);
    generateVideoMutation.mutate({
      prompt: videoPrompt,
      style: selectedStyle,
      duration: videoDuration
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'generating':
        return 'bg-yellow-100 text-yellow-800';
      case 'completed':
        return 'bg-green-100 text-green-800';
      case 'failed':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">AI Studio</h1>
          <p className="text-lg text-gray-600">Generate music, videos, and integrate with web3 platforms</p>
        </div>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="audio">Audio Generator</TabsTrigger>
            <TabsTrigger value="video">Video Generator</TabsTrigger>
            <TabsTrigger value="integrations">Integrations</TabsTrigger>
            <TabsTrigger value="gallery">Gallery</TabsTrigger>
          </TabsList>

          <TabsContent value="audio" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Music className="w-5 h-5" />
                  AI Music Generator
                </CardTitle>
                <CardDescription>Generate techno, drum & bass, and other electronic music</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <div>
                      <Label htmlFor="audio-prompt">Music Description</Label>
                      <Textarea
                        id="audio-prompt"
                        placeholder="Describe the music you want to generate..."
                        value={audioPrompt}
                        onChange={(e) => setAudioPrompt(e.target.value)}
                        rows={4}
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="genre">Genre</Label>
                      <Select value={selectedGenre} onValueChange={setSelectedGenre}>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a genre" />
                        </SelectTrigger>
                        <SelectContent>
                          {musicGenres.map(genre => (
                            <SelectItem key={genre.id} value={genre.id}>
                              <div>
                                <div className="font-medium">{genre.name}</div>
                                <div className="text-sm text-gray-500">{genre.description}</div>
                              </div>
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>

                    <div>
                      <Label htmlFor="duration">Duration (seconds)</Label>
                      <Input
                        id="duration"
                        type="number"
                        min="10"
                        max="300"
                        value={audioDuration}
                        onChange={(e) => setAudioDuration(Number(e.target.value))}
                      />
                    </div>
                  </div>

                  <div className="space-y-4">
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <h4 className="font-medium mb-2">Featured Genres</h4>
                      <div className="grid grid-cols-2 gap-2">
                        {musicGenres.slice(0, 4).map(genre => (
                          <Card key={genre.id} className="p-3 hover:bg-gray-100 cursor-pointer transition-colors">
                            <div className="flex items-center gap-2">
                              <AudioWaveform className="w-4 h-4 text-blue-500" />
                              <span className="text-sm font-medium">{genre.name}</span>
                            </div>
                          </Card>
                        ))}
                      </div>
                    </div>

                    <div className="p-4 bg-blue-50 rounded-lg">
                      <h4 className="font-medium mb-2 flex items-center gap-2">
                        <Sparkles className="w-4 h-4 text-blue-500" />
                        Pro Tips
                      </h4>
                      <ul className="text-sm text-gray-600 space-y-1">
                        <li>• Be specific about the mood and energy level</li>
                        <li>• Mention specific instruments or sounds</li>
                        <li>• Include tempo preferences (BPM)</li>
                        <li>• Reference other artists or songs for style</li>
                      </ul>
                    </div>
                  </div>
                </div>

                <div className="flex gap-4">
                  <Button 
                    onClick={handleGenerateAudio}
                    disabled={isGenerating || !audioPrompt || !selectedGenre}
                    className="flex items-center gap-2"
                  >
                    {isGenerating ? (
                      <>
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                        Generating...
                      </>
                    ) : (
                      <>
                        <Play className="w-4 h-4" />
                        Generate Audio
                      </>
                    )}
                  </Button>
                  
                  <Button variant="outline" className="flex items-center gap-2">
                    <Upload className="w-4 h-4" />
                    Upload Sample
                  </Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="video" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Video className="w-5 h-5" />
                  AI Video Generator
                </CardTitle>
                <CardDescription>Generate videos from text descriptions</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <div>
                      <Label htmlFor="video-prompt">Video Description</Label>
                      <Textarea
                        id="video-prompt"
                        placeholder="Describe the video you want to generate..."
                        value={videoPrompt}
                        onChange={(e) => setVideoPrompt(e.target.value)}
                        rows={4}
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="style">Style</Label>
                      <Select value={selectedStyle} onValueChange={setSelectedStyle}>
                        <SelectTrigger>
                          <SelectValue placeholder="Select a style" />
                        </SelectTrigger>
                        <SelectContent>
                          {videoStyles.map(style => (
                            <SelectItem key={style.id} value={style.id}>
                              <div>
                                <div className="font-medium">{style.name}</div>
                                <div className="text-sm text-gray-500">{style.description}</div>
                              </div>
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>

                    <div>
                      <Label htmlFor="video-duration">Duration (seconds)</Label>
                      <Input
                        id="video-duration"
                        type="number"
                        min="5"
                        max="60"
                        value={videoDuration}
                        onChange={(e) => setVideoDuration(Number(e.target.value))}
                      />
                    </div>
                  </div>

                  <div className="space-y-4">
                    <div className="p-4 bg-gray-50 rounded-lg">
                      <h4 className="font-medium mb-2">Popular Styles</h4>
                      <div className="grid grid-cols-1 gap-2">
                        {videoStyles.slice(0, 3).map(style => (
                          <Card key={style.id} className="p-3 hover:bg-gray-100 cursor-pointer transition-colors">
                            <div className="flex items-center gap-2">
                              <Film className="w-4 h-4 text-purple-500" />
                              <span className="text-sm font-medium">{style.name}</span>
                            </div>
                          </Card>
                        ))}
                      </div>
                    </div>

                    <div className="p-4 bg-purple-50 rounded-lg">
                      <h4 className="font-medium mb-2 flex items-center gap-2">
                        <Camera className="w-4 h-4 text-purple-500" />
                        Video Tips
                      </h4>
                      <ul className="text-sm text-gray-600 space-y-1">
                        <li>• Describe scenes with specific details</li>
                        <li>• Mention camera angles and movements</li>
                        <li>• Include lighting and mood preferences</li>
                        <li>• Keep descriptions concise but vivid</li>
                      </ul>
                    </div>
                  </div>
                </div>

                <div className="flex gap-4">
                  <Button 
                    onClick={handleGenerateVideo}
                    disabled={isGenerating || !videoPrompt || !selectedStyle}
                    className="flex items-center gap-2"
                  >
                    {isGenerating ? (
                      <>
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                        Generating...
                      </>
                    ) : (
                      <>
                        <Play className="w-4 h-4" />
                        Generate Video
                      </>
                    )}
                  </Button>
                  
                  <Button variant="outline" className="flex items-center gap-2">
                    <Settings className="w-4 h-4" />
                    Advanced Settings
                  </Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="integrations" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {integrations.map(integration => {
                const IconComponent = integration.icon;
                return (
                  <Card key={integration.id} className="hover:shadow-lg transition-shadow">
                    <CardHeader>
                      <div className="flex items-center gap-3">
                        <div className={`p-3 rounded-lg ${integration.color}`}>
                          <IconComponent className="w-6 h-6 text-white" />
                        </div>
                        <div>
                          <CardTitle className="text-lg">{integration.name}</CardTitle>
                          <CardDescription>{integration.description}</CardDescription>
                        </div>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-4">
                        <div>
                          <h4 className="font-medium mb-2">Features</h4>
                          <ul className="space-y-1">
                            {integration.features.map((feature, index) => (
                              <li key={index} className="text-sm text-gray-600 flex items-center gap-2">
                                <Zap className="w-3 h-3 text-blue-500" />
                                {feature}
                              </li>
                            ))}
                          </ul>
                        </div>
                        
                        <div className="flex gap-2">
                          <Button 
                            onClick={() => handleIntegrationAction(integration.id, 'connect')}
                            className="flex-1"
                          >
                            Connect
                          </Button>
                          <Button 
                            variant="outline" 
                            onClick={() => handleIntegrationAction(integration.id, 'docs')}
                          >
                            Docs
                          </Button>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Integration Hub</CardTitle>
                <CardDescription>Connect with your favorite web3 and productivity platforms</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="p-4 bg-gradient-to-r from-purple-50 to-blue-50 rounded-lg">
                    <h4 className="font-medium mb-2">Web3 Integrations</h4>
                    <p className="text-sm text-gray-600 mb-3">
                      Connect with blockchain platforms for NFTs, credentials, and decentralized apps.
                    </p>
                    <Button size="sm" className="w-full">
                      Explore Web3
                    </Button>
                  </div>
                  
                  <div className="p-4 bg-gradient-to-r from-green-50 to-yellow-50 rounded-lg">
                    <h4 className="font-medium mb-2">Productivity Tools</h4>
                    <p className="text-sm text-gray-600 mb-3">
                      Sync with your favorite note-taking and organization apps.
                    </p>
                    <Button size="sm" className="w-full">
                      Browse Tools
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="gallery" className="space-y-6">
            <div className="text-center">
              <h2 className="text-xl font-semibold text-gray-900 mb-2">Your Generations</h2>
              <p className="text-gray-600">View and manage your generated content</p>
            </div>

            {generationTasks.length === 0 ? (
              <Card>
                <CardContent className="p-8 text-center">
                  <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Sparkles className="w-8 h-8 text-gray-400" />
                  </div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">No generations yet</h3>
                  <p className="text-gray-600 mb-4">Start creating AI-generated music and videos to see them here.</p>
                  <div className="flex gap-2 justify-center">
                    <Button onClick={() => setActiveTab('audio')}>
                      <Music className="w-4 h-4 mr-2" />
                      Generate Audio
                    </Button>
                    <Button onClick={() => setActiveTab('video')} variant="outline">
                      <Video className="w-4 h-4 mr-2" />
                      Generate Video
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {generationTasks.map((task) => (
                  <Card key={task.id} className="hover:shadow-lg transition-shadow">
                    <CardHeader className="pb-3">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          {task.type === 'audio' ? (
                            <FileAudio className="w-5 h-5 text-blue-500" />
                          ) : (
                            <FileVideo className="w-5 h-5 text-purple-500" />
                          )}
                          <CardTitle className="text-lg capitalize">{task.type}</CardTitle>
                        </div>
                        <Badge className={getStatusColor(task.status)}>
                          {task.status}
                        </Badge>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-3">
                        <p className="text-sm text-gray-600 line-clamp-2">{task.prompt}</p>
                        
                        {task.progress > 0 && task.progress < 100 && (
                          <div>
                            <div className="flex justify-between text-sm mb-1">
                              <span>Progress</span>
                              <span>{task.progress}%</span>
                            </div>
                            <Progress value={task.progress} className="h-2" />
                          </div>
                        )}
                        
                        <div className="flex gap-2">
                          {task.status === 'completed' && (
                            <>
                              <Button size="sm" variant="outline" className="flex-1">
                                <Play className="w-4 h-4 mr-1" />
                                Play
                              </Button>
                              <Button size="sm" variant="outline">
                                <Download className="w-4 h-4" />
                              </Button>
                            </>
                          )}
                          {task.status === 'generating' && (
                            <Button size="sm" variant="outline" disabled className="flex-1">
                              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-current mr-2"></div>
                              Generating...
                            </Button>
                          )}
                        </div>
                        
                        <div className="text-xs text-gray-500">
                          {new Date(task.createdAt).toLocaleString()}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}