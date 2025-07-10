import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { 
  Image, 
  DollarSign, 
  Share2, 
  Heart, 
  Eye, 
  Tag,
  Wallet,
  ExternalLink,
  Upload,
  Filter,
  Search,
  TrendingUp,
  Users,
  Clock,
  Star,
  ShoppingCart,
  Zap,
  Globe,
  Copy,
  Check
} from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { useMutation, useQuery } from "@tanstack/react-query";
import { apiRequest } from "@/lib/queryClient";

interface NFT {
  id: string;
  tokenId: string;
  contractAddress: string;
  name: string;
  description: string;
  imageUrl: string;
  price: number;
  currency: string;
  owner: string;
  creator: string;
  isListed: boolean;
  collection: string;
  rarity: string;
  attributes: Array<{
    trait_type: string;
    value: string;
  }>;
  createdAt: string;
  lastSale?: {
    price: number;
    date: string;
  };
  views: number;
  likes: number;
  chain: string;
}

interface Collection {
  id: string;
  name: string;
  description: string;
  imageUrl: string;
  floorPrice: number;
  totalVolume: number;
  itemCount: number;
  ownerCount: number;
  chain: string;
}

const chains = [
  { id: 'ethereum', name: 'Ethereum', symbol: 'ETH' },
  { id: 'polygon', name: 'Polygon', symbol: 'MATIC' },
  { id: 'bsc', name: 'BNB Chain', symbol: 'BNB' },
  { id: 'arbitrum', name: 'Arbitrum', symbol: 'ETH' },
  { id: 'optimism', name: 'Optimism', symbol: 'ETH' }
];

const rarityColors = {
  'Common': 'bg-gray-100 text-gray-800',
  'Uncommon': 'bg-green-100 text-green-800',
  'Rare': 'bg-blue-100 text-blue-800',
  'Epic': 'bg-purple-100 text-purple-800',
  'Legendary': 'bg-yellow-100 text-yellow-800'
};

export default function NFTMarketplace() {
  const [selectedTab, setSelectedTab] = useState('marketplace');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedChain, setSelectedChain] = useState('all');
  const [selectedCollection, setSelectedCollection] = useState('all');
  const [priceRange, setPriceRange] = useState({ min: '', max: '' });
  const [sortBy, setSortBy] = useState('recent');
  const [isConnected, setIsConnected] = useState(false);
  const [walletAddress, setWalletAddress] = useState('');
  const [copied, setCopied] = useState(false);
  const [selectedNFT, setSelectedNFT] = useState<NFT | null>(null);
  const [listingPrice, setListingPrice] = useState('');
  const [listingCurrency, setListingCurrency] = useState('ETH');
  
  const { toast } = useToast();

  // Mock data for demonstration
  const mockNFTs: NFT[] = [
    {
      id: '1',
      tokenId: '1234',
      contractAddress: '0x123...abc',
      name: 'Cosmic Warrior #1234',
      description: 'A legendary cosmic warrior from the outer realms',
      imageUrl: '/api/placeholder/400/400',
      price: 2.5,
      currency: 'ETH',
      owner: '0x456...def',
      creator: '0x789...ghi',
      isListed: true,
      collection: 'Cosmic Warriors',
      rarity: 'Legendary',
      attributes: [
        { trait_type: 'Background', value: 'Cosmic' },
        { trait_type: 'Armor', value: 'Legendary' },
        { trait_type: 'Weapon', value: 'Plasma Sword' }
      ],
      createdAt: '2024-01-15',
      lastSale: { price: 1.8, date: '2024-01-10' },
      views: 1250,
      likes: 89,
      chain: 'ethereum'
    },
    {
      id: '2',
      tokenId: '5678',
      contractAddress: '0x234...bcd',
      name: 'Digital Landscape #5678',
      description: 'A beautiful digital landscape with vibrant colors',
      imageUrl: '/api/placeholder/400/400',
      price: 0.8,
      currency: 'ETH',
      owner: '0x567...efg',
      creator: '0x890...hij',
      isListed: true,
      collection: 'Digital Landscapes',
      rarity: 'Rare',
      attributes: [
        { trait_type: 'Style', value: 'Abstract' },
        { trait_type: 'Color Scheme', value: 'Vibrant' },
        { trait_type: 'Composition', value: 'Landscape' }
      ],
      createdAt: '2024-01-12',
      views: 680,
      likes: 45,
      chain: 'polygon'
    }
  ];

  const mockCollections: Collection[] = [
    {
      id: '1',
      name: 'Cosmic Warriors',
      description: 'A collection of legendary warriors from the cosmos',
      imageUrl: '/api/placeholder/300/300',
      floorPrice: 1.2,
      totalVolume: 245.8,
      itemCount: 10000,
      ownerCount: 3456,
      chain: 'ethereum'
    },
    {
      id: '2',
      name: 'Digital Landscapes',
      description: 'Beautiful digital art landscapes and environments',
      imageUrl: '/api/placeholder/300/300',
      floorPrice: 0.5,
      totalVolume: 89.2,
      itemCount: 5000,
      ownerCount: 1234,
      chain: 'polygon'
    }
  ];

  const connectWallet = async () => {
    try {
      // Simulate wallet connection
      setIsConnected(true);
      setWalletAddress('0x1234...5678');
      toast({
        title: "Wallet Connected",
        description: "Successfully connected to your wallet",
      });
    } catch (error) {
      toast({
        title: "Connection Failed",
        description: "Failed to connect wallet. Please try again.",
        variant: "destructive",
      });
    }
  };

  const connectOKX = async () => {
    try {
      // Simulate OKX connection
      toast({
        title: "OKX Connected",
        description: "Successfully connected to OKX Marketplace",
      });
    } catch (error) {
      toast({
        title: "OKX Connection Failed",
        description: "Failed to connect to OKX. Please check your API keys.",
        variant: "destructive",
      });
    }
  };

  const handleCopyAddress = () => {
    navigator.clipboard.writeText(walletAddress);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const listNFTMutation = useMutation({
    mutationFn: async (data: any) => {
      return await apiRequest('/api/nft/list', {
        method: 'POST',
        body: JSON.stringify(data),
      });
    },
    onSuccess: () => {
      toast({
        title: "NFT Listed",
        description: "Your NFT has been successfully listed for sale",
      });
    },
    onError: () => {
      toast({
        title: "Listing Failed",
        description: "Failed to list NFT. Please try again.",
        variant: "destructive",
      });
    },
  });

  const handleListNFT = (nft: NFT) => {
    if (!listingPrice) return;
    
    listNFTMutation.mutate({
      tokenId: nft.tokenId,
      contractAddress: nft.contractAddress,
      price: parseFloat(listingPrice),
      currency: listingCurrency,
    });
  };

  const shareNFT = (nft: NFT) => {
    const shareUrl = `${window.location.origin}/nft/${nft.id}`;
    navigator.clipboard.writeText(shareUrl);
    toast({
      title: "Link Copied",
      description: "NFT sharing link copied to clipboard",
    });
  };

  const filteredNFTs = mockNFTs.filter(nft => {
    const matchesSearch = nft.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         nft.collection.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesChain = selectedChain === 'all' || nft.chain === selectedChain;
    const matchesCollection = selectedCollection === 'all' || nft.collection === selectedCollection;
    const matchesPrice = (!priceRange.min || nft.price >= parseFloat(priceRange.min)) &&
                        (!priceRange.max || nft.price <= parseFloat(priceRange.max));
    
    return matchesSearch && matchesChain && matchesCollection && matchesPrice;
  });

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2">
                <Image className="w-8 h-8 text-blue-600" />
                <h1 className="text-2xl font-bold text-gray-900">NFT Marketplace</h1>
              </div>
            </div>
            
            <div className="flex items-center gap-4">
              {!isConnected ? (
                <div className="flex gap-2">
                  <Button onClick={connectWallet} className="flex items-center gap-2">
                    <Wallet className="w-4 h-4" />
                    Connect Wallet
                  </Button>
                  <Button onClick={connectOKX} variant="outline" className="flex items-center gap-2">
                    <ExternalLink className="w-4 h-4" />
                    Connect OKX
                  </Button>
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <Badge variant="outline" className="flex items-center gap-2">
                    <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                    Connected
                  </Badge>
                  <Button 
                    variant="outline" 
                    onClick={handleCopyAddress}
                    className="flex items-center gap-2"
                  >
                    {copied ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
                    {walletAddress}
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Tabs value={selectedTab} onValueChange={setSelectedTab}>
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="marketplace">Marketplace</TabsTrigger>
            <TabsTrigger value="collections">Collections</TabsTrigger>
            <TabsTrigger value="my-nfts">My NFTs</TabsTrigger>
            <TabsTrigger value="create">Create</TabsTrigger>
          </TabsList>

          <TabsContent value="marketplace" className="space-y-6">
            {/* Filters */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Filter className="w-5 h-5" />
                  Filters
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                  <div>
                    <Label htmlFor="search">Search</Label>
                    <div className="relative">
                      <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                      <Input
                        id="search"
                        placeholder="Search NFTs..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="pl-10"
                      />
                    </div>
                  </div>
                  
                  <div>
                    <Label htmlFor="chain">Chain</Label>
                    <Select value={selectedChain} onValueChange={setSelectedChain}>
                      <SelectTrigger>
                        <SelectValue placeholder="All Chains" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="all">All Chains</SelectItem>
                        {chains.map(chain => (
                          <SelectItem key={chain.id} value={chain.id}>
                            {chain.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  
                  <div>
                    <Label htmlFor="collection">Collection</Label>
                    <Select value={selectedCollection} onValueChange={setSelectedCollection}>
                      <SelectTrigger>
                        <SelectValue placeholder="All Collections" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="all">All Collections</SelectItem>
                        {mockCollections.map(collection => (
                          <SelectItem key={collection.id} value={collection.name}>
                            {collection.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  
                  <div>
                    <Label htmlFor="min-price">Min Price</Label>
                    <Input
                      id="min-price"
                      type="number"
                      placeholder="0.0"
                      value={priceRange.min}
                      onChange={(e) => setPriceRange({...priceRange, min: e.target.value})}
                    />
                  </div>
                  
                  <div>
                    <Label htmlFor="max-price">Max Price</Label>
                    <Input
                      id="max-price"
                      type="number"
                      placeholder="100.0"
                      value={priceRange.max}
                      onChange={(e) => setPriceRange({...priceRange, max: e.target.value})}
                    />
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* NFT Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {filteredNFTs.map(nft => (
                <Card key={nft.id} className="group hover:shadow-lg transition-shadow cursor-pointer">
                  <div className="aspect-square bg-gray-100 rounded-t-lg overflow-hidden">
                    <div className="w-full h-full bg-gradient-to-br from-blue-400 to-purple-600 flex items-center justify-center">
                      <Image className="w-20 h-20 text-white opacity-50" />
                    </div>
                  </div>
                  
                  <CardContent className="p-4">
                    <div className="flex items-start justify-between mb-2">
                      <div>
                        <h3 className="font-semibold text-lg truncate">{nft.name}</h3>
                        <p className="text-sm text-gray-600">{nft.collection}</p>
                      </div>
                      <Badge className={rarityColors[nft.rarity as keyof typeof rarityColors]}>
                        {nft.rarity}
                      </Badge>
                    </div>
                    
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center gap-1">
                        <DollarSign className="w-4 h-4 text-green-600" />
                        <span className="font-bold text-lg">{nft.price} {nft.currency}</span>
                      </div>
                      <div className="flex items-center gap-4 text-sm text-gray-500">
                        <div className="flex items-center gap-1">
                          <Eye className="w-4 h-4" />
                          {nft.views}
                        </div>
                        <div className="flex items-center gap-1">
                          <Heart className="w-4 h-4" />
                          {nft.likes}
                        </div>
                      </div>
                    </div>
                    
                    <div className="flex gap-2">
                      <Button 
                        size="sm" 
                        className="flex-1"
                        onClick={() => setSelectedNFT(nft)}
                      >
                        <ShoppingCart className="w-4 h-4 mr-1" />
                        Buy Now
                      </Button>
                      <Button 
                        size="sm" 
                        variant="outline"
                        onClick={() => shareNFT(nft)}
                      >
                        <Share2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="collections" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {mockCollections.map(collection => (
                <Card key={collection.id} className="hover:shadow-lg transition-shadow cursor-pointer">
                  <div className="aspect-square bg-gray-100 rounded-t-lg overflow-hidden">
                    <div className="w-full h-full bg-gradient-to-br from-green-400 to-blue-600 flex items-center justify-center">
                      <Image className="w-20 h-20 text-white opacity-50" />
                    </div>
                  </div>
                  
                  <CardContent className="p-4">
                    <h3 className="font-bold text-xl mb-2">{collection.name}</h3>
                    <p className="text-gray-600 mb-4">{collection.description}</p>
                    
                    <div className="grid grid-cols-2 gap-4 mb-4">
                      <div>
                        <p className="text-sm text-gray-500">Floor Price</p>
                        <p className="font-semibold">{collection.floorPrice} ETH</p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-500">Total Volume</p>
                        <p className="font-semibold">{collection.totalVolume} ETH</p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-500">Items</p>
                        <p className="font-semibold">{collection.itemCount.toLocaleString()}</p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-500">Owners</p>
                        <p className="font-semibold">{collection.ownerCount.toLocaleString()}</p>
                      </div>
                    </div>
                    
                    <Button className="w-full">
                      <Eye className="w-4 h-4 mr-2" />
                      View Collection
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="my-nfts" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Wallet className="w-5 h-5" />
                  My NFTs
                </CardTitle>
                <CardDescription>
                  Manage your NFT collection and list items for sale
                </CardDescription>
              </CardHeader>
              <CardContent>
                {!isConnected ? (
                  <div className="text-center py-8">
                    <Wallet className="w-16 h-16 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-600 mb-4">Connect your wallet to view your NFTs</p>
                    <Button onClick={connectWallet}>Connect Wallet</Button>
                  </div>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {mockNFTs.map(nft => (
                      <Card key={nft.id} className="hover:shadow-lg transition-shadow">
                        <div className="aspect-square bg-gray-100 rounded-t-lg overflow-hidden">
                          <div className="w-full h-full bg-gradient-to-br from-purple-400 to-pink-600 flex items-center justify-center">
                            <Image className="w-20 h-20 text-white opacity-50" />
                          </div>
                        </div>
                        
                        <CardContent className="p-4">
                          <h3 className="font-semibold text-lg truncate mb-2">{nft.name}</h3>
                          <p className="text-sm text-gray-600 mb-3">{nft.collection}</p>
                          
                          <div className="flex gap-2">
                            <Dialog>
                              <DialogTrigger asChild>
                                <Button size="sm" className="flex-1">
                                  <Tag className="w-4 h-4 mr-1" />
                                  List for Sale
                                </Button>
                              </DialogTrigger>
                              <DialogContent>
                                <DialogHeader>
                                  <DialogTitle>List NFT for Sale</DialogTitle>
                                  <DialogDescription>
                                    Set a price for your NFT and list it on the marketplace
                                  </DialogDescription>
                                </DialogHeader>
                                <div className="space-y-4">
                                  <div>
                                    <Label htmlFor="listing-price">Price</Label>
                                    <Input
                                      id="listing-price"
                                      type="number"
                                      placeholder="0.0"
                                      value={listingPrice}
                                      onChange={(e) => setListingPrice(e.target.value)}
                                    />
                                  </div>
                                  <div>
                                    <Label htmlFor="listing-currency">Currency</Label>
                                    <Select value={listingCurrency} onValueChange={setListingCurrency}>
                                      <SelectTrigger>
                                        <SelectValue />
                                      </SelectTrigger>
                                      <SelectContent>
                                        <SelectItem value="ETH">ETH</SelectItem>
                                        <SelectItem value="MATIC">MATIC</SelectItem>
                                        <SelectItem value="BNB">BNB</SelectItem>
                                      </SelectContent>
                                    </Select>
                                  </div>
                                  <Button 
                                    onClick={() => handleListNFT(nft)}
                                    disabled={!listingPrice || listNFTMutation.isPending}
                                    className="w-full"
                                  >
                                    {listNFTMutation.isPending ? 'Listing...' : 'List NFT'}
                                  </Button>
                                </div>
                              </DialogContent>
                            </Dialog>
                            <Button 
                              size="sm" 
                              variant="outline"
                              onClick={() => shareNFT(nft)}
                            >
                              <Share2 className="w-4 h-4" />
                            </Button>
                          </div>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="create" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Upload className="w-5 h-5" />
                  Create NFT
                </CardTitle>
                <CardDescription>
                  Upload your artwork and mint it as an NFT
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center">
                  <Upload className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600 mb-2">Drop your files here or click to upload</p>
                  <p className="text-sm text-gray-500">PNG, JPG, GIF up to 10MB</p>
                  <Button className="mt-4" variant="outline">
                    Choose Files
                  </Button>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <div>
                      <Label htmlFor="nft-name">Name</Label>
                      <Input id="nft-name" placeholder="Enter NFT name" />
                    </div>
                    
                    <div>
                      <Label htmlFor="nft-description">Description</Label>
                      <Textarea 
                        id="nft-description" 
                        placeholder="Enter description" 
                        rows={4} 
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="nft-collection">Collection</Label>
                      <Select>
                        <SelectTrigger>
                          <SelectValue placeholder="Select collection" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="new">Create New Collection</SelectItem>
                          {mockCollections.map(collection => (
                            <SelectItem key={collection.id} value={collection.name}>
                              {collection.name}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <Label htmlFor="nft-price">Price</Label>
                      <Input id="nft-price" type="number" placeholder="0.0" />
                    </div>
                    
                    <div>
                      <Label htmlFor="nft-currency">Currency</Label>
                      <Select>
                        <SelectTrigger>
                          <SelectValue placeholder="Select currency" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="ETH">ETH</SelectItem>
                          <SelectItem value="MATIC">MATIC</SelectItem>
                          <SelectItem value="BNB">BNB</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    
                    <div>
                      <Label htmlFor="nft-chain">Blockchain</Label>
                      <Select>
                        <SelectTrigger>
                          <SelectValue placeholder="Select blockchain" />
                        </SelectTrigger>
                        <SelectContent>
                          {chains.map(chain => (
                            <SelectItem key={chain.id} value={chain.id}>
                              {chain.name}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                </div>
                
                <div className="flex justify-end gap-4">
                  <Button variant="outline">Save as Draft</Button>
                  <Button className="flex items-center gap-2">
                    <Zap className="w-4 h-4" />
                    Create NFT
                  </Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      {/* NFT Details Modal */}
      {selectedNFT && (
        <Dialog open={!!selectedNFT} onOpenChange={() => setSelectedNFT(null)}>
          <DialogContent className="max-w-4xl">
            <DialogHeader>
              <DialogTitle>{selectedNFT.name}</DialogTitle>
              <DialogDescription>
                {selectedNFT.collection} • {selectedNFT.rarity}
              </DialogDescription>
            </DialogHeader>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="aspect-square bg-gray-100 rounded-lg overflow-hidden">
                <div className="w-full h-full bg-gradient-to-br from-blue-400 to-purple-600 flex items-center justify-center">
                  <Image className="w-24 h-24 text-white opacity-50" />
                </div>
              </div>
              
              <div className="space-y-4">
                <div>
                  <h3 className="font-semibold text-lg mb-2">Description</h3>
                  <p className="text-gray-600">{selectedNFT.description}</p>
                </div>
                
                <div>
                  <h3 className="font-semibold text-lg mb-2">Attributes</h3>
                  <div className="grid grid-cols-2 gap-2">
                    {selectedNFT.attributes.map((attr, index) => (
                      <div key={index} className="bg-gray-50 p-2 rounded">
                        <p className="text-sm text-gray-500">{attr.trait_type}</p>
                        <p className="font-medium">{attr.value}</p>
                      </div>
                    ))}
                  </div>
                </div>
                
                <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                  <div>
                    <p className="text-sm text-gray-500">Current Price</p>
                    <p className="text-2xl font-bold">{selectedNFT.price} {selectedNFT.currency}</p>
                  </div>
                  <Button className="flex items-center gap-2">
                    <ShoppingCart className="w-4 h-4" />
                    Buy Now
                  </Button>
                </div>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
}