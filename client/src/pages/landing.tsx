import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Shield, Users, Lock, BarChart3, Settings, HelpCircle } from "lucide-react";

export default function Landing() {
  const handleLogin = () => {
    window.location.href = "/api/login";
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-blue-100">
      {/* Hero Section */}
      <section className="py-16 sm:py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl font-bold text-gray-900 sm:text-5xl md:text-6xl">
              <span className="block">Secure Authentication</span>
              <span className="block text-blue-600">Made Simple</span>
            </h1>
            <p className="mt-6 max-w-md mx-auto text-base text-gray-500 sm:text-lg md:mt-8 md:text-xl md:max-w-3xl">
              Experience seamless authentication with robust security features. Protected routes and user management built for modern applications.
            </p>
            <div className="mt-10 max-w-sm mx-auto sm:max-w-none sm:flex sm:justify-center">
              <div className="space-y-4 sm:space-y-0 sm:mx-auto sm:inline-grid sm:grid-cols-2 sm:gap-5">
                <Button
                  onClick={handleLogin}
                  className="flex items-center justify-center px-8 py-3 text-base font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 transition-colors"
                >
                  Get Started
                </Button>
                <Button
                  variant="outline"
                  className="flex items-center justify-center px-8 py-3 text-base font-medium rounded-lg text-blue-700 bg-blue-100 hover:bg-blue-200 transition-colors border-blue-200"
                >
                  Learn More
                </Button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h2 className="text-3xl font-bold text-gray-900 sm:text-4xl">
              Authentication Features
            </h2>
            <p className="mt-4 text-lg text-gray-500">
              Everything you need for secure user authentication and session management
            </p>
          </div>
          
          <div className="mt-12 grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
            <div className="text-center">
              <div className="flex items-center justify-center w-16 h-16 bg-blue-100 rounded-lg mx-auto">
                <Lock className="w-8 h-8 text-blue-600" />
              </div>
              <h3 className="mt-4 text-lg font-medium text-gray-900">Secure Authentication</h3>
              <p className="mt-2 text-base text-gray-500">
                OAuth-based authentication with industry-standard security protocols
              </p>
            </div>
            
            <div className="text-center">
              <div className="flex items-center justify-center w-16 h-16 bg-blue-100 rounded-lg mx-auto">
                <Shield className="w-8 h-8 text-blue-600" />
              </div>
              <h3 className="mt-4 text-lg font-medium text-gray-900">Protected Routes</h3>
              <p className="mt-2 text-base text-gray-500">
                Secure your application with route-level authentication and authorization
              </p>
            </div>
            
            <div className="text-center">
              <div className="flex items-center justify-center w-16 h-16 bg-blue-100 rounded-lg mx-auto">
                <Users className="w-8 h-8 text-blue-600" />
              </div>
              <h3 className="mt-4 text-lg font-medium text-gray-900">User Management</h3>
              <p className="mt-2 text-base text-gray-500">
                Complete user profile management with persistent sessions
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <div className="max-w-md mx-auto">
              <Lock className="mx-auto h-12 w-12 text-gray-400" />
              <h3 className="mt-2 text-sm font-medium text-gray-900">Authentication Required</h3>
              <p className="mt-1 text-sm text-gray-500">Please sign in to access your dashboard and protected content.</p>
              <div className="mt-6">
                <Button
                  onClick={handleLogin}
                  className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  <Lock className="mr-2 -ml-1 w-5 h-5" />
                  Sign In
                </Button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200">
        <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div className="col-span-1 md:col-span-2">
              <h3 className="text-lg font-semibold text-gray-900">SecureApp</h3>
              <p className="mt-2 text-base text-gray-500">
                Building secure applications with modern authentication patterns and best practices.
              </p>
            </div>
            <div>
              <h4 className="text-sm font-semibold text-gray-900 tracking-wider uppercase">Features</h4>
              <ul className="mt-4 space-y-2">
                <li><a href="#" className="text-base text-gray-500 hover:text-gray-900">Authentication</a></li>
                <li><a href="#" className="text-base text-gray-500 hover:text-gray-900">Protected Routes</a></li>
                <li><a href="#" className="text-base text-gray-500 hover:text-gray-900">User Management</a></li>
              </ul>
            </div>
            <div>
              <h4 className="text-sm font-semibold text-gray-900 tracking-wider uppercase">Support</h4>
              <ul className="mt-4 space-y-2">
                <li><a href="#" className="text-base text-gray-500 hover:text-gray-900">Documentation</a></li>
                <li><a href="#" className="text-base text-gray-500 hover:text-gray-900">Help Center</a></li>
                <li><a href="#" className="text-base text-gray-500 hover:text-gray-900">Contact</a></li>
              </ul>
            </div>
          </div>
          <div className="mt-8 border-t border-gray-200 pt-8">
            <p className="text-base text-gray-400 text-center">
              &copy; 2024 SecureApp. All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
