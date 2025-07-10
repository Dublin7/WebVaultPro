# DevVault Pro

## Overview

DevVault Pro is a comprehensive web application built with React/TypeScript frontend and Node.js/Express backend. The application implements secure authentication using Replit's OpenID Connect (OIDC) system and provides multiple developer tools including a shell command generator, AI agents system, and AI studio for media generation. The project follows a monorepo structure with shared schemas and utilities.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Frontend Architecture
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite for development and production builds
- **Styling**: Tailwind CSS with shadcn/ui component library
- **Routing**: Wouter for client-side routing
- **State Management**: React Query (@tanstack/react-query) for server state management
- **UI Components**: Radix UI primitives with custom styling via shadcn/ui

### Backend Architecture
- **Runtime**: Node.js with Express.js framework
- **Language**: TypeScript with ES modules
- **Database**: PostgreSQL with Drizzle ORM
- **Authentication**: Replit OIDC with Passport.js
- **Session Management**: Express sessions with PostgreSQL storage

### Database Layer
- **ORM**: Drizzle ORM for type-safe database operations
- **Database**: PostgreSQL (via Neon serverless)
- **Migrations**: Drizzle Kit for schema migrations
- **Connection**: Connection pooling with @neondatabase/serverless

## Key Components

### Authentication System
- **Provider**: Replit OIDC authentication
- **Strategy**: OpenID Connect with Passport.js
- **Session Storage**: PostgreSQL-backed sessions using connect-pg-simple
- **User Management**: User profiles stored in PostgreSQL with automatic upsert

### Database Schema
- **Users Table**: Stores user profiles with email, names, and profile images
- **Sessions Table**: Handles secure session storage (required for Replit Auth)
- **Agents Table**: AI agents with configurations and execution status
- **Tasks Table**: Task management for AI agents with progress tracking
- **Agent Logs Table**: Detailed logging for AI agent activities
- **Schema Validation**: Zod schemas for type safety and validation

### AI Studio & Media Generation
- **Audio Generator**: Creates techno, drum & bass, and electronic music
- **Video Generator**: Generates videos from text descriptions with multiple styles
- **File Management**: Automatic file organization and storage
- **Integration Hub**: Connects with Galxe, Metaschool, and Google Keep
- **Synthetic Media**: Creates WAV audio files and MP4 video placeholders

### AI Agents System
- **Agent Types**: Code Saver, Document Manager, Web Scraper, Data Processor, File Organizer
- **Task Execution**: Real-time task processing with progress tracking
- **File Operations**: Automated file creation, organization, and management
- **Live Controls**: Start, stop, pause agents with real-time status updates
- **Logging System**: Comprehensive activity logging and monitoring

### Shell Command Generator
- **Template System**: Customizable command templates with variables
- **Example Library**: Practical examples for development, Git, system monitoring
- **AI Integration**: Natural language to shell command conversion
- **Copy Functionality**: One-click command copying to clipboard

### UI Components
- **Design System**: shadcn/ui components built on Radix UI
- **Styling**: Tailwind CSS with CSS variables for theming
- **Responsive Design**: Mobile-first approach with responsive breakpoints
- **Accessibility**: ARIA-compliant components from Radix UI

### API Layer
- **REST API**: Express.js routes for authentication and user management
- **Error Handling**: Centralized error handling middleware
- **Request Logging**: Custom middleware for API request logging
- **Type Safety**: Shared TypeScript types between frontend and backend

## Data Flow

1. **Authentication Flow**:
   - User initiates login via `/api/login`
   - Replit OIDC handles authentication
   - Callback creates/updates user record
   - Session established with PostgreSQL storage

2. **Data Management**:
   - Frontend uses React Query for API state management
   - Backend provides RESTful endpoints
   - Database operations use Drizzle ORM
   - Type safety maintained with shared schemas

3. **User Interface**:
   - React components consume authenticated user data
   - Conditional rendering based on authentication state
   - Protected routes redirect unauthenticated users

## External Dependencies

### Core Dependencies
- **Authentication**: Replit OIDC service
- **Database**: PostgreSQL (Neon serverless)
- **UI Library**: Radix UI primitives
- **Build Tools**: Vite, TypeScript, Tailwind CSS

### Development Tools
- **Type Checking**: TypeScript with strict configuration
- **Code Quality**: ESLint configuration for React/TypeScript
- **Database Tools**: Drizzle Kit for migrations and schema management

### Optional OAuth Servers
- Two example OAuth servers (Node.js and Python) for GitHub integration
- These appear to be development/reference implementations

## Deployment Strategy

### Development Environment
- **Dev Server**: Vite development server with HMR
- **API Server**: Express server with TypeScript compilation via tsx
- **Database**: Local or cloud PostgreSQL instance

### Production Build
- **Frontend**: Vite builds static assets to `dist/public`
- **Backend**: ESBuild bundles server code to `dist/index.js`
- **Database**: Migrations applied via Drizzle Kit
- **Environment**: NODE_ENV=production with optimized builds

### Environment Requirements
- **DATABASE_URL**: PostgreSQL connection string
- **SESSION_SECRET**: Secure session signing key
- **REPLIT_DOMAINS**: Required for Replit authentication
- **ISSUER_URL**: OIDC issuer URL (defaults to Replit)

### Replit Integration
- **Development**: Replit-specific Vite plugins for cartographer and runtime error overlay
- **Authentication**: Seamless integration with Replit user accounts
- **Deployment**: Configured for Replit's hosting environment

The application is designed to be easily deployable on Replit while maintaining the flexibility to run in other environments with proper environment variable configuration.