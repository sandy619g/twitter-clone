# twitter-clone

## 🖥️ Backend Setup (Spring Boot)

The backend is built using [Spring Boot](https://spring.io/projects/spring-boot) and provides a Restful API for the frontend to interact with.

### 📦 Prerequisites

Before setting up the backend, ensure you have the following installed:

- [Java 21](https://adoptopenjdk.net/) (Spring Boot 3.x requires Java 17 or later)
- [Maven](https://maven.apache.org/)
- [Postman](https://www.postman.com/) (optional, for testing the API)

### 🚀 Getting Started

1. Clone the repository (if you haven't already):
   ```bash
   git clone https://github.com/sandy619g/twitter-clone.git
   cd twitter-clone
   ```
2. Navigate to the backend folder:
   ```bash
   cd twitter-backend
   ```
3. Build the application using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
The backend will now be running at http://localhost:8080 by default.


## 🌐 Frontend Setup (React + Vite)

The frontend is built using [React](https://reactjs.org/) with [Vite](https://vitejs.dev/) for fast development and hot reloading.

### 📦 Prerequisites

- [Node.js](https://nodejs.org/) (version 16+ recommended)
- npm (comes with Node.js) or yarn

### 🚀 Getting Started

1. Navigate to the frontend directory:
   ```bash
   cd twitter-frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   npm install axios react-router-dom
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Open your browser and visit:
   ```bash
   http://localhost:5173
   ```      

