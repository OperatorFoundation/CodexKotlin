# CodexKotlin

CodexKotlin is a Kotlin implementation of Operator's Codex, a text-to-text format-transforming-encryption library. CodexKotlin enables encoding arbitrary data into various constrained text formats for transmission through restrictive channels.

## Overview

CodexKotlin is part of the Operator Foundation's Codex project, which provides format-transforming encryption capabilities. This library encodes binary data into structured text formats that can pass through systems with strict formatting requirements, such as:

- WSPR (Weak Signal Propagation Reporter) messages for amateur radio
- SMS-compatible text formats
- Email-safe encodings
- Other constrained communication channels

## Installation

Add to your `build.gradle`:

```gradle
dependencies {
    implementation 'org.operatorfoundation:codex:1.0.0'
}
```

## Core Components

### Symbol System

CodexKotlin uses a flexible symbol system that defines encoding constraints for different formats. Symbols can represent:
- Fixed characters (Required)
- Letter/number combinations (CallLetterNumber)
- Grid coordinates (GridLetter)
- Numeric values (Number)
- Power levels (Power)

### Encoder/Decoder

The core `Encoder` and `Decoder` classes transform between binary data and symbol-constrained formats:

```kotlin
val symbols = listOf(/* symbol definitions */)
val encoder = Encoder(symbols)
val decoder = Decoder(symbols)

// Encode data to symbols
val data = BigInteger("12345")
val encoded = encoder.encode(data)

// Decode back
val decoded = decoder.decode(encoded)
```

## WSPR Implementation

The library includes a complete WSPR message codec as a reference implementation:

### WSPRCodex

Encodes arbitrary binary data into WSPR message format.

```kotlin
import org.operatorfoundation.codex.WSPRCodex

val codex = WSPRCodex()

// Encode any binary data
val data = "Hello World!".toByteArray()
val messages = codex.encode(data)

// Messages are in WSPR format
messages.forEach { msg ->
    println("Callsign: ${msg.callsign}")
    println("Grid: ${msg.gridSquare}")
    println("Power: ${msg.powerDbm} dBm")
}

// Decode back to original
val decoded = codex.decode(messages)
```

### Multi-Message Support

For data larger than a single format frame, CodexKotlin automatically chunks and reassembles:

```kotlin
val largeData = ByteArray(100) { it.toByte() }
val messages = codex.encode(largeData)  // Automatically chunks

// Handles out-of-order reception
val shuffled = messages.shuffled()
val decoded = codex.decode(shuffled)  // Reassembles correctly
```

### Capacity

WSPR mode supports:
- **Basic Mode**: Up to 64 bytes (16 messages × 4 bytes/message)
- **Extended Mode**: Up to 768 bytes (256 messages × 3 bytes/message)

## Custom Format Support

You can define your own symbol constraints for other formats:

```kotlin
// Define symbols for your format
val customSymbols = listOf(
    Required('!'.code.toByte()),  // Fixed prefix
    Letter(),                      // A-Z only
    Number(),                      // 0-9 only
    AlphaNumeric()                 // A-Z, 0-9
)

val encoder = Encoder(customSymbols)
val decoder = Decoder(customSymbols)
```

## Architecture

```
CodexKotlin
├── Core
│   ├── Encoder/Decoder    - Core transformation engine
│   ├── Symbol System      - Format constraint definitions
│   └── BigInteger Math    - Numeric representation
│
└── Implementations
    ├── WSPRCodex          - WSPR radio messages (public API)
    ├── WSPRMultiMessageCodex - Chunking/reassembly (internal class)
    └── [Extensible for other formats]
```

## Use Cases

- **Amateur Radio**: Send data via WSPR, FT8, or other weak-signal modes
- **Emergency Communications**: Encode messages for transmission through limited channels
- **Steganography**: Hide data within format-compliant text
- **IoT Telemetry**: Send sensor data through SMS or other text-only channels
- **Censorship Circumvention**: Transform data to pass through restrictive filters

## API Reference

### WSPRCodex Methods

#### `encode(data: ByteArray, messageId: Byte? = null): List<WSPRDataMessage>`

Encodes binary data into WSPR messages.

**Parameters:**
- `data`: Binary data to encode
- `messageId`: Optional ID for message grouping (auto-generated if null)

**Returns:** List of WSPR messages

#### `decode(messages: List<WSPRDataMessage>): ByteArray`

Decodes WSPR messages back to binary data.

**Parameters:**
- `messages`: List of WSPR messages (order doesn't matter)

**Returns:** Original binary data

### WSPRDataMessage Properties

```kotlin
data class WSPRDataMessage(
    val callsign: String,    // 6 characters max
    val gridSquare: String,   // 4 characters (Maidenhead)
    val powerDbm: Int        // 0-60 dBm
)
```

## Transmission Time

Each WSPR message requires 2 minutes to transmit:

| Data Size | Messages | Transmission Time |
|-----------|----------|------------------|
| 1-4 bytes | 1 | 2 minutes |
| 5-8 bytes | 2 | 4 minutes |
| 20 bytes | 5 | 10 minutes |
| 64 bytes | 16 | 32 minutes |
| 768 bytes | 256 | 8.5 hours |

## Error Handling

```kotlin
try {
    val encoded = codex.encode(data)
} catch (e: WSPRCodexException) {
    // Handle encoding errors
}

try {
    val decoded = codex.decode(messages)
} catch (e: WSPRMultiMessageException) {
    // Handle incomplete or corrupted messages
}
```

## Example: Encrypted Communication

```kotlin
import org.operatorfoundation.codex.WSPRCodex
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun encryptAndTransmit(plaintext: String, key: ByteArray) {
    // Encrypt
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"))
    val encrypted = cipher.doFinal(plaintext.toByteArray())
    
    // Encode to WSPR
    val codex = WSPRCodex()
    val messages = codex.encode(encrypted)
    
    // Transmit each message
    messages.forEach { msg ->
        transmitWSPR(msg.callsign, msg.gridSquare, msg.powerDbm)
    }
}

fun receiveAndDecrypt(messages: List<WSPRDataMessage>, key: ByteArray): String {
    // Decode from WSPR
    val codex = WSPRCodex()
    val encrypted = codex.decode(messages)
    
    // Decrypt
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"))
    val decrypted = cipher.doFinal(encrypted)
    
    return String(decrypted)
}
```

## Limitations

- Some formats may lose trailing zeros in data
- Each format has maximum capacity constraints
- All chunks must be received for successful multi-message decoding
- WSPR format limited to ~50 bits per message

## Contributing

CodexKotlin is part of the Operator Foundation's suite of tools for unrestricted internet access. Contributions are welcome.

## Related Projects

- **Codex** (Swift): Original implementation
- **CodexPython**: Python implementation
- **AudioCoder**: Audio signal generation for encoded messages
- **TransmissionAndroid**: Android USB serial communication

## License

MIT License
