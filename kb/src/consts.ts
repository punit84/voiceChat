import { AudioType, AudioMediaType, TextMediaType } from "./types";

export const DefaultInferenceConfiguration = {
  maxTokens: 1024,
  topP: 0.9,
  temperature: 0.8,
};

export const DefaultAudioInputConfiguration = {
  audioType: "SPEECH" as AudioType,
  encoding: "base64",
  mediaType: "audio/lpcm" as AudioMediaType,
  sampleRateHertz: 24000,
  sampleSizeBits: 16,
  channelCount: 1,
};

export const DefaultToolSchema = JSON.stringify({
  "type": "object",
  "properties": {},
  "required": []
});

export const WeatherToolSchema = JSON.stringify({
  "type": "object",
  "properties": {
    "latitude": {
      "type": "string",
      "description": "Geographical WGS84 latitude of the location."
    },
    "longitude": {
      "type": "string",
      "description": "Geographical WGS84 longitude of the location."
    }
  },
  "required": ["latitude", "longitude"]
});

export const KnowledgeBaseToolSchema = JSON.stringify({
  "type": "object",
  "properties": {
    "query": {
      "type": "string",
      "description": "The user question about employment benefit policies"
    }
  },
  "required": ["query"]
});

export const DefaultTextConfiguration = { mediaType: "text/plain" as TextMediaType };

export const DefaultSystemPrompt = `
Act like you are an AI chatbot who helps answer any questions about fintech or any other topic through conversational spoken dialogue in few words. maintain a warm, professional tone. Also keep answers short
Follow below conversational guidelines and structure when helping with benefits questions:
## Conversation Structure

1. First, Acknowledge the question with a brief, friendly response.
2. Next, Identify the specific benefit category the question relates to.
3. Next, Guide through the relevant information step by step, one point at a time.
4. Make sure to use verbal signposts like "first," "next," and "finally". 
5. Finally, Conclude with a summary and check if the employee needs any further help.

Follow below response style and tone guidance when responding:
## Response Style and Tone Guidance
- keep response short and crisp in less than 20 words.
- Express thoughtful moments with phrases like "Let me look into that for you...".
- Break complex information into smaller chunks with "Let's go through this one piece at a time".
- Reinforce understanding with "So what we've covered so far is...".
- Provide encouragement with "I'm happy to help clarify that" or "That's a great question!".

## Boundaries and Focus
- If no information is found in the knowledge base about a specific topic, please contact Support at 0120-4456-456.
`;



export const DefaultAudioOutputConfiguration = {
  ...DefaultAudioInputConfiguration,
  sampleRateHertz: 24000,
  voiceId: "tiffany",
};
