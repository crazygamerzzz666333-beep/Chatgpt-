# ChatGPT Fabric Mod 🤖

A Minecraft Fabric mod that adds a real ChatGPT-powered AI assistant into your Minecraft chat. Ask questions and get intelligent responses directly in-game!

## ✨ Features

- 🤖 Real ChatGPT AI integration using OpenAI API
- ⚡ Async non-blocking API calls (doesn't freeze the game)
- 🎮 Works on multiplayer servers, singleplayer, and PojavLauncher
- ⚙️ Fully configurable (model, system prompt, max tokens, timeout)
- 🔄 Automatic config generation on first run
- 💬 Multiline response support with smart formatting
- ⏱️ Per-player cooldowns (5 seconds default)
- 🔐 Permission level system (0+ for chat, 3+ for reload)
- 🎯 Graceful error handling with helpful messages
- 🚀 Production-ready and fully tested

## 📋 Requirements

- **Minecraft**: 1.21.1
- **Fabric Loader**: 0.15.11+
- **Java**: 21+
- **Fabric API**: 0.100.0+1.21.1

## 🔧 Installation

1. **Download Fabric Installer** from [fabricmc.net](https://fabricmc.net/)
2. **Install Fabric Loader** for Minecraft 1.21.1
3. **Download ChatGPT Fabric mod** (JAR file)
4. **Place JAR** in your `mods` folder
5. **Launch Minecraft** (config will be auto-generated)
6. **Edit config** at `config/chatgptfabric/config.json`
7. **Add your OpenAI API key** to the config
8. **Restart** or use `/gptreload` command

## 🔑 OpenAI API Setup

1. Go to [platform.openai.com](https://platform.openai.com)
2. Sign up or log in
3. Navigate to API keys section
4. Create a new API key
5. Copy your API key
6. Edit `config/chatgptfabric/config.json`:
   ```json
   {
     "api_key": "sk-YOUR-API-KEY-HERE",
     "model": "gpt-4-mini",
     "system_prompt": "You are a helpful Minecraft assistant. Keep responses concise and friendly.",
     "max_tokens": 512,
     "timeout_seconds": 30
   }
   ```
7. Save and restart or use `/gptreload`

## 🎮 Usage

### Commands

```
/gpt <question>          - Ask ChatGPT a question
/gptreload              - Reload configuration (OP only)
```

### Examples

```
/gpt What is the best way to find diamonds in Minecraft?
/gpt How do I build a good house?
/gpt Tell me a Minecraft joke
```

## 🔨 Building from Source

### Build Steps

```bash
git clone https://github.com/crazygamerzzz666333-beep/Chatgpt-
cd Chatgpt-
./gradlew build
```

Output: `build/libs/chatgptfabric-1.0.0.jar`

## ⚙️ Configuration

The config file is located at `config/chatgptfabric/config.json`:

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `api_key` | String | `PUT_API_KEY_HERE` | Your OpenAI API key (required) |
| `model` | String | `gpt-4-mini` | ChatGPT model to use |
| `system_prompt` | String | Helpful assistant message | System prompt for the AI |
| `max_tokens` | Integer | `512` | Max tokens per response (256-4096) |
| `timeout_seconds` | Integer | `30` | API request timeout in seconds |

## 📝 License

MIT License - See LICENSE file for details
