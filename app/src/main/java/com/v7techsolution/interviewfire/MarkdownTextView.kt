package com.v7techsolution.interviewfire

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import java.util.regex.Pattern

class MarkdownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "MarkdownTextView"

        // VS Code Dark Theme Colors
        private val keywordColor = Color.parseColor("#569cd6")     // Blue
        private val functionColor = Color.parseColor("#dcdcaa")    // Yellow
        private val stringColor = Color.parseColor("#ce9178")      // Orange
        private val commentColor = Color.parseColor("#6a9955")     // Green
        private val numberColor = Color.parseColor("#b5cea8")      // Light green
        private val typeColor = Color.parseColor("#4ec9b0")        // Teal
        private val operatorColor = Color.parseColor("#d4d4d4")    // Light gray
        private val bracketColor = Color.parseColor("#ffd700")     // Gold
    }

    init {
        // Set default text properties for regular text only
        setTextColor(Color.parseColor("#333333"))
        setLineSpacing(1.2f, 1.0f)
        // Don't apply any special formatting to regular text
    }

    fun setMarkdownText(markdown: String) {
        val spannableText = parseMarkdown(markdown)
        setText(spannableText)
    }

    private fun parseMarkdown(markdown: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        Log.d(TAG, "Parsing markdown: ${markdown.take(200)}...")

        // Split the markdown into lines for processing
        val lines = markdown.lines()
        var inCodeBlock = false
        var codeBlockLanguage = ""
        val codeBlockContent = StringBuilder()

        for ((index, line) in lines.withIndex()) {
            Log.d(TAG, "Processing line $index: '$line'")

            when {
                // Code block start (```language or just ```)
                line.trim().startsWith("```") -> {
                    val trimmedLine = line.trim()
                    if (!inCodeBlock) {
                        inCodeBlock = true
                        codeBlockLanguage = trimmedLine.removePrefix("```").trim()
                        Log.d(TAG, "Starting code block with language: '$codeBlockLanguage'")
                    } else {
                        // This is the closing ```
                        inCodeBlock = false
                        // Process the code block
                        if (codeBlockContent.isNotEmpty()) {
                            Log.d(TAG, "Processing code block with content: ${codeBlockContent.toString().take(100)}...")
                            processCodeBlock(builder, codeBlockContent.toString().trim(), codeBlockLanguage)
                            codeBlockContent.clear()
                        }
                        codeBlockLanguage = ""
                    }
                }

                // Inside code block
                inCodeBlock -> {
                    Log.d(TAG, "Adding to code block: '$line'")
                    codeBlockContent.append(line).append("\n")
                }

                // Regular markdown line
                else -> {
                    val processedLine = processInlineMarkdown(line)
                    builder.append(processedLine)

                    // Add line break unless it's the last line
                    if (index < lines.size - 1) {
                        builder.append("\n")
                    }
                }
            }
        }

        // Handle unclosed code block
        if (inCodeBlock && codeBlockContent.isNotEmpty()) {
            Log.d(TAG, "Processing unclosed code block")
            processCodeBlock(builder, codeBlockContent.toString().trim(), codeBlockLanguage)
        }

        Log.d(TAG, "Final markdown result length: ${builder.length}")
        return builder
    }

    private fun processCodeBlock(builder: SpannableStringBuilder, code: String, language: String) {
        // Add some spacing before code block
        if (builder.isNotEmpty()) {
            builder.append("\n")
        }

        // Create editor-like header with language indicator
        val headerText = if (language.isNotEmpty()) " $language " else " code "
        builder.append("┌─" + "─".repeat(headerText.length) + "─┐\n")
        builder.append("│$headerText│\n")
        builder.append("├─" + "─".repeat(headerText.length) + "─┤\n")

        // Add line numbers and code
        val lines = code.lines()
        val maxLineNumber = lines.size
        val lineNumberWidth = maxLineNumber.toString().length

        for ((index, line) in lines.withIndex()) {
            val lineNumber = (index + 1).toString().padStart(lineNumberWidth, ' ')
            builder.append("│ $lineNumber │ $line\n")
        }

        // Close the editor box
        builder.append("└─" + "─".repeat(lineNumberWidth + 2) + "─┴─" + "─".repeat(50) + "─┘\n")

        // Apply syntax highlighting based on language
        val highlightedCode = applySyntaxHighlighting(code, language)

        // Find the code content within the editor box and apply highlighting
        val editorContent = builder.toString()
        val codeStartIndex = editorContent.indexOf("│ 1 │ ")
        if (codeStartIndex != -1) {
            val codeEndIndex = editorContent.lastIndexOf("└─")
            if (codeEndIndex > codeStartIndex) {
                // Apply syntax highlighting to the actual code content only
                var currentLineStart = codeStartIndex
                for ((index, line) in lines.withIndex()) {
                    if (line.isNotEmpty()) {
                        val lineStart = currentLineStart + "│ ${index + 1} │ ".length
                        val lineEnd = lineStart + line.length

                        if (lineEnd <= builder.length) {
                            // Apply monospace font to code content only
                            builder.setSpan(
                                TypefaceSpan("monospace"),
                                lineStart,
                                lineEnd,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            // Apply syntax highlighting to this specific line
                            applySyntaxHighlightingToLine(builder, line, language, lineStart, lineEnd)
                        }
                    }
                    currentLineStart += "│ ${index + 1} │ $line\n".length
                }
            }
        }

        // Add some spacing after code block
        builder.append("\n")
    }

    private fun applySyntaxHighlighting(code: String, language: String): SpannableStringBuilder {
        val highlightedBuilder = SpannableStringBuilder(code)

        when (language.lowercase()) {
            "dockerfile", "docker" -> applyDockerHighlighting(highlightedBuilder)
            "yaml", "yml" -> applyYamlHighlighting(highlightedBuilder)
            "python", "py" -> applyPythonHighlighting(highlightedBuilder)
            "javascript", "js" -> applyJavaScriptHighlighting(highlightedBuilder)
            "bash", "shell", "sh" -> applyBashHighlighting(highlightedBuilder)
            "kotlin" -> applyKotlinHighlighting(highlightedBuilder)
            "java" -> applyJavaHighlighting(highlightedBuilder)
            else -> applyGenericHighlighting(highlightedBuilder)
        }

        return highlightedBuilder
    }

    private fun applySyntaxHighlightingToLine(builder: SpannableStringBuilder, line: String, language: String, start: Int, end: Int) {
        // Apply syntax highlighting to a specific line within the editor box
        when (language.lowercase()) {
            "dockerfile", "docker" -> applyDockerHighlightingToLine(builder, line, start, end)
            "yaml", "yml" -> applyYamlHighlightingToLine(builder, line, start, end)
            "python", "py" -> applyPythonHighlightingToLine(builder, line, start, end)
            "javascript", "js" -> applyJavaScriptHighlightingToLine(builder, line, start, end)
            "bash", "shell", "sh" -> applyBashHighlightingToLine(builder, line, start, end)
            "kotlin" -> applyKotlinHighlightingToLine(builder, line, start, end)
            "java" -> applyJavaHighlightingToLine(builder, line, start, end)
            else -> applyGenericHighlightingToLine(builder, line, start, end)
        }
    }

    private fun applyDockerHighlighting(builder: SpannableStringBuilder) {
        // Docker instructions
        val instructions = listOf("FROM", "RUN", "CMD", "ENTRYPOINT", "COPY", "ADD", "WORKDIR", "EXPOSE", "ENV", "ARG", "USER", "VOLUME", "LABEL")
        applyKeywordHighlighting(builder, instructions, keywordColor)

        // Strings (after keywords)
        applyStringHighlighting(builder)

        // Comments
        applyCommentHighlighting(builder)
    }

    private fun applyYamlHighlighting(builder: SpannableStringBuilder) {
        // YAML keys
        val keyPattern = Pattern.compile("^(\\s*)([\\w-]+):", Pattern.MULTILINE)
        val keyMatcher = keyPattern.matcher(builder)

        while (keyMatcher.find()) {
            val keyStart = keyMatcher.start(2)
            val keyEnd = keyMatcher.end(2)
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#4ec9b0")), // Teal for keys
                keyStart,
                keyEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Values
        applyStringHighlighting(builder)
        applyCommentHighlighting(builder)
    }

    private fun applyPythonHighlighting(builder: SpannableStringBuilder) {
        // Python keywords
        val keywords = listOf("def", "class", "if", "else", "elif", "for", "while", "try", "except", "finally", "with", "as", "import", "from", "return", "yield", "lambda", "and", "or", "not", "in", "is", "True", "False", "None")
        applyKeywordHighlighting(builder, keywords, keywordColor)

        // Function definitions
        val functionPattern = Pattern.compile("\\bdef\\s+([\\w_]+)\\s*\\(")
        val functionMatcher = functionPattern.matcher(builder)

        while (functionMatcher.find()) {
            val funcStart = functionMatcher.start(1)
            val funcEnd = functionMatcher.end(1)
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                funcStart,
                funcEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Strings and comments
        applyStringHighlighting(builder)
        applyCommentHighlighting(builder)
    }

    private fun applyJavaScriptHighlighting(builder: SpannableStringBuilder) {
        // JavaScript keywords
        val keywords = listOf("function", "const", "let", "var", "if", "else", "for", "while", "try", "catch", "finally", "return", "async", "await", "class", "extends", "import", "export", "default")
        applyKeywordHighlighting(builder, keywords, keywordColor)

        // Function names
        val functionPattern = Pattern.compile("\\bfunction\\s+([\\w_]+)|\\b([\\w_]+)\\s*\\(")
        val functionMatcher = functionPattern.matcher(builder)

        while (functionMatcher.find()) {
            val funcStart = if (functionMatcher.group(1) != null) functionMatcher.start(1) else functionMatcher.start(2)
            val funcEnd = if (functionMatcher.group(1) != null) functionMatcher.end(1) else functionMatcher.end(2)
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                funcStart,
                funcEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Strings and comments
        applyStringHighlighting(builder)
        applyCommentHighlighting(builder)
    }

    private fun applyBashHighlighting(builder: SpannableStringBuilder) {
        // Bash commands
        val commands = listOf("docker", "kubectl", "git", "npm", "yarn", "ls", "cd", "mkdir", "rm", "cp", "mv", "echo", "cat", "grep", "find", "chmod", "chown", "ps", "kill", "curl", "wget")
        applyKeywordHighlighting(builder, commands, Color.parseColor("#4ec9b0")) // Teal for commands

        // Options (starting with -)
        val optionPattern = Pattern.compile("\\s(-{1,2}[\\w-]+)")
        val optionMatcher = optionPattern.matcher(builder)

        while (optionMatcher.find()) {
            builder.setSpan(
                ForegroundColorSpan(Color.parseColor("#b5cea8")), // Light green for options
                optionMatcher.start(1),
                optionMatcher.end(1),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Strings and comments
        applyStringHighlighting(builder)
        applyCommentHighlighting(builder)
    }

    private fun applyKotlinHighlighting(builder: SpannableStringBuilder) {
        // Kotlin keywords
        val keywords = listOf("fun", "val", "var", "class", "interface", "object", "if", "else", "when", "for", "while", "try", "catch", "finally", "return", "import", "package", "as", "is", "in", "true", "false", "null")
        applyKeywordHighlighting(builder, keywords, keywordColor)

        // Function definitions
        val functionPattern = Pattern.compile("\\bfun\\s+([\\w_]+)\\s*\\(")
        val functionMatcher = functionPattern.matcher(builder)

        while (functionMatcher.find()) {
            val funcStart = functionMatcher.start(1)
            val funcEnd = functionMatcher.end(1)
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                funcStart,
                funcEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Strings and comments
        applyStringHighlighting(builder)
        applyCommentHighlighting(builder)
    }

    private fun applyJavaHighlighting(builder: SpannableStringBuilder) {
        // Java keywords
        val keywords = listOf("public", "private", "protected", "class", "interface", "extends", "implements", "static", "final", "void", "int", "String", "boolean", "if", "else", "for", "while", "try", "catch", "finally", "return", "import", "package", "new", "this", "super", "true", "false", "null")
        applyKeywordHighlighting(builder, keywords, keywordColor)

        // Method definitions
        val methodPattern = Pattern.compile("\\b(public|private|protected)?\\s*(static)?\\s*(final)?\\s*[\\w\\[\\]<>]+\\s+([\\w_]+)\\s*\\(")
        val methodMatcher = methodPattern.matcher(builder)

        while (methodMatcher.find()) {
            val methodStart = methodMatcher.start(4)
            val methodEnd = methodMatcher.end(4)
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                methodStart,
                methodEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Strings and comments
        applyStringHighlighting(builder)
        applyCommentHighlighting(builder)
    }

    private fun applyGenericHighlighting(builder: SpannableStringBuilder) {
        // Basic highlighting for unknown languages
        applyStringHighlighting(builder)
        applyCommentHighlighting(builder)
    }

    private fun applyKeywordHighlighting(builder: SpannableStringBuilder, keywords: List<String>, color: Int) {
        for (keyword in keywords) {
            val pattern = Pattern.compile("\\b$keyword\\b")
            val matcher = pattern.matcher(builder)

            while (matcher.find()) {
                builder.setSpan(
                    ForegroundColorSpan(color),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun applyDockerHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        val instructions = listOf("FROM", "RUN", "CMD", "ENTRYPOINT", "COPY", "ADD", "WORKDIR", "EXPOSE", "ENV", "ARG", "USER", "VOLUME", "LABEL")

        instructions.forEach { instruction ->
            var index = 0
            while (index < text.length) {
                val foundIndex = text.indexOf(instruction, index, ignoreCase = true)
                if (foundIndex == -1) break

                val absoluteStart = start + foundIndex
                val absoluteEnd = absoluteStart + instruction.length

                if (absoluteEnd <= builder.length) {
                    builder.setSpan(
                        ForegroundColorSpan(keywordColor),
                        absoluteStart,
                        absoluteEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                index = foundIndex + instruction.length
            }
        }

        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyYamlHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        val keyPattern = Pattern.compile("^(\\s*)([\\w-]+):", Pattern.MULTILINE)
        val keyMatcher = keyPattern.matcher(text)

        while (keyMatcher.find()) {
            val keyStart = start + keyMatcher.start(2)
            val keyEnd = start + keyMatcher.end(2)
            builder.setSpan(
                ForegroundColorSpan(typeColor),
                keyStart,
                keyEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyPythonHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        val keywords = listOf("def", "class", "if", "else", "elif", "for", "while", "try", "except", "finally", "with", "as", "import", "from", "return", "yield", "lambda", "and", "or", "not", "in", "is", "True", "False", "None")

        keywords.forEach { keyword ->
            var index = 0
            while (index < text.length) {
                val foundIndex = text.indexOf(keyword, index)
                if (foundIndex == -1) break

                val absoluteStart = start + foundIndex
                val absoluteEnd = absoluteStart + keyword.length

                if (absoluteEnd <= builder.length) {
                    builder.setSpan(
                        ForegroundColorSpan(keywordColor),
                        absoluteStart,
                        absoluteEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                index = foundIndex + keyword.length
            }
        }

        // Function definitions
        val functionPattern = Pattern.compile("\\bdef\\s+([\\w_]+)\\s*\\(")
        val functionMatcher = functionPattern.matcher(text)

        while (functionMatcher.find()) {
            val funcStart = start + functionMatcher.start(1)
            val funcEnd = start + functionMatcher.end(1)
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                funcStart,
                funcEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyJavaScriptHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        val keywords = listOf("function", "const", "let", "var", "if", "else", "for", "while", "try", "catch", "finally", "return", "async", "await", "class", "extends", "import", "export", "default")

        keywords.forEach { keyword ->
            var index = 0
            while (index < text.length) {
                val foundIndex = text.indexOf(keyword, index)
                if (foundIndex == -1) break

                val absoluteStart = start + foundIndex
                val absoluteEnd = absoluteStart + keyword.length

                if (absoluteEnd <= builder.length) {
                    builder.setSpan(
                        ForegroundColorSpan(keywordColor),
                        absoluteStart,
                        absoluteEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                index = foundIndex + keyword.length
            }
        }

        // Function names
        val functionPattern = Pattern.compile("\\bfunction\\s+([\\w_]+)|\\b([\\w_]+)\\s*\\(")
        val functionMatcher = functionPattern.matcher(text)

        while (functionMatcher.find()) {
            val funcStart = if (functionMatcher.group(1) != null) start + functionMatcher.start(1) else start + functionMatcher.start(2)
            val funcEnd = if (functionMatcher.group(1) != null) start + functionMatcher.end(1) else start + functionMatcher.end(2)
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                funcStart,
                funcEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyBashHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        val commands = listOf("docker", "kubectl", "git", "npm", "yarn", "node", "python", "pip", "apt", "yum", "echo", "cd", "ls", "cp", "mv", "rm", "mkdir", "chmod", "chown", "grep", "find", "curl", "wget")

        commands.forEach { command ->
            var index = 0
            while (index < text.length) {
                val foundIndex = text.indexOf(command, index)
                if (foundIndex == -1) break

                val absoluteStart = start + foundIndex
                val absoluteEnd = absoluteStart + command.length

                if (absoluteEnd <= builder.length) {
                    builder.setSpan(
                        ForegroundColorSpan(typeColor),
                        absoluteStart,
                        absoluteEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                index = foundIndex + command.length
            }
        }

        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyKotlinHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        val keywords = listOf("fun", "val", "var", "class", "interface", "object", "if", "else", "when", "for", "while", "try", "catch", "finally", "import", "package", "return", "null", "true", "false")

        keywords.forEach { keyword ->
            var index = 0
            while (index < text.length) {
                val foundIndex = text.indexOf(keyword, index)
                if (foundIndex == -1) break

                val absoluteStart = start + foundIndex
                val absoluteEnd = absoluteStart + keyword.length

                if (absoluteEnd <= builder.length) {
                    builder.setSpan(
                        ForegroundColorSpan(keywordColor),
                        absoluteStart,
                        absoluteEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                index = foundIndex + keyword.length
            }
        }

        // Function definitions
        val functionPattern = Pattern.compile("\\bfun\\s+([\\w_]+)\\s*\\(")
        val functionMatcher = functionPattern.matcher(text)

        while (functionMatcher.find()) {
            val funcStart = start + functionMatcher.start(1)
            val funcEnd = start + functionMatcher.end(1)
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                funcStart,
                funcEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyJavaHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        val keywords = listOf("public", "private", "protected", "class", "interface", "extends", "implements", "static", "final", "void", "int", "String", "boolean", "if", "else", "for", "while", "try", "catch", "finally", "return", "import", "package", "new", "this", "super", "true", "false", "null")

        keywords.forEach { keyword ->
            var index = 0
            while (index < text.length) {
                val foundIndex = text.indexOf(keyword, index)
                if (foundIndex == -1) break

                val absoluteStart = start + foundIndex
                val absoluteEnd = absoluteStart + keyword.length

                if (absoluteEnd <= builder.length) {
                    builder.setSpan(
                        ForegroundColorSpan(keywordColor),
                        absoluteStart,
                        absoluteEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                index = foundIndex + keyword.length
            }
        }

        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyGenericHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        applyStringHighlightingToLine(builder, text, start, end)
        applyCommentHighlightingToLine(builder, text, start, end)
    }

    private fun applyStringHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        // Double quoted strings
        val doubleQuotePattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"")
        val doubleQuoteMatcher = doubleQuotePattern.matcher(text)

        while (doubleQuoteMatcher.find()) {
            val absoluteStart = start + doubleQuoteMatcher.start()
            val absoluteEnd = start + doubleQuoteMatcher.end()
            builder.setSpan(
                ForegroundColorSpan(stringColor),
                absoluteStart,
                absoluteEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Single quoted strings
        val singleQuotePattern = Pattern.compile("'([^'\\\\]|\\\\.)*'")
        val singleQuoteMatcher = singleQuotePattern.matcher(text)

        while (singleQuoteMatcher.find()) {
            val absoluteStart = start + singleQuoteMatcher.start()
            val absoluteEnd = start + singleQuoteMatcher.end()
            builder.setSpan(
                ForegroundColorSpan(stringColor),
                absoluteStart,
                absoluteEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun applyCommentHighlightingToLine(builder: SpannableStringBuilder, text: String, start: Int, end: Int) {
        // Single line comments
        val commentPatterns = listOf(
            Pattern.compile("#.*$", Pattern.MULTILINE),  // Python/Bash style
            Pattern.compile("//.*$", Pattern.MULTILINE), // JavaScript/Java style
            Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL) // Multi-line comments
        )

        for (pattern in commentPatterns) {
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val absoluteStart = start + matcher.start()
                val absoluteEnd = start + matcher.end()
                builder.setSpan(
                    ForegroundColorSpan(commentColor),
                    absoluteStart,
                    absoluteEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun applyStringHighlighting(builder: SpannableStringBuilder) {
        // Double quoted strings
        val doubleQuotePattern = Pattern.compile("\"([^\"\\\\]|\\\\.)*\"")
        val doubleQuoteMatcher = doubleQuotePattern.matcher(builder)

        while (doubleQuoteMatcher.find()) {
            builder.setSpan(
                ForegroundColorSpan(stringColor),
                doubleQuoteMatcher.start(),
                doubleQuoteMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Single quoted strings
        val singleQuotePattern = Pattern.compile("'([^'\\\\]|\\\\.)*'")
        val singleQuoteMatcher = singleQuotePattern.matcher(builder)

        while (singleQuoteMatcher.find()) {
            builder.setSpan(
                ForegroundColorSpan(stringColor),
                singleQuoteMatcher.start(),
                singleQuoteMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun applyCommentHighlighting(builder: SpannableStringBuilder) {
        // Single line comments
        val commentPatterns = listOf(
            Pattern.compile("#.*$", Pattern.MULTILINE),  // Python/Bash style
            Pattern.compile("//.*$", Pattern.MULTILINE), // JavaScript/Java style
            Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL) // Multi-line comments
        )

        for (pattern in commentPatterns) {
            val matcher = pattern.matcher(builder)
            while (matcher.find()) {
                builder.setSpan(
                    ForegroundColorSpan(commentColor),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    private fun processInlineMarkdown(line: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(line)

        // Process headers (# ## ###) - keep basic formatting
        val headerPattern = Pattern.compile("^(#{1,6})\\s+(.+)$", Pattern.MULTILINE)
        val headerMatcher = headerPattern.matcher(line)

        if (headerMatcher.find()) {
            val level = headerMatcher.group(1)?.length ?: 1
            val headerText = headerMatcher.group(2) ?: ""

            builder.clear()
            builder.append(headerText)

            // Apply subtle header styling (keep it readable)
            when (level) {
                1 -> builder.setSpan(StyleSpan(Typeface.BOLD), 0, headerText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                2 -> builder.setSpan(StyleSpan(Typeface.BOLD), 0, headerText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                else -> {} // No special styling for H3+
            }

            return builder
        }

        // Process inline code (`code`) - subtle highlighting only
        val inlineCodePattern = Pattern.compile("`([^`]+)`")
        val inlineCodeMatcher = inlineCodePattern.matcher(line)

        var lastEnd = 0
        val newBuilder = SpannableStringBuilder()

        while (inlineCodeMatcher.find()) {
            // Add text before the code
            newBuilder.append(line.substring(lastEnd, inlineCodeMatcher.start()))

            // Add the inline code with minimal styling
            val codeText = inlineCodeMatcher.group(1) ?: ""
            val codeStart = newBuilder.length
            newBuilder.append(codeText)
            val codeEnd = newBuilder.length

            // Apply minimal inline code styling (just monospace)
            newBuilder.setSpan(
                TypefaceSpan("monospace"),
                codeStart,
                codeEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            lastEnd = inlineCodeMatcher.end()
        }

        // Add remaining text
        if (lastEnd < line.length) {
            newBuilder.append(line.substring(lastEnd))
        }

        // If no inline code was found, return the original line
        if (newBuilder.isEmpty()) {
            newBuilder.append(line)
        }

        // Process bold (**text** or __text__) - keep basic
        processBoldText(newBuilder)

        // Process italic (*text* or _text_) - keep basic
        processItalicText(newBuilder)

        return newBuilder
    }

    private fun processBoldText(builder: SpannableStringBuilder) {
        val boldPatterns = arrayOf(
            Pattern.compile("\\*\\*([^\\*]+)\\*\\*"),  // **text**
            Pattern.compile("__([^_]+)__")             // __text__
        )

        for (pattern in boldPatterns) {
            val matcher = pattern.matcher(builder)
            val spansToAdd = mutableListOf<Triple<Int, Int, StyleSpan>>()

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                val textStart = matcher.start(1)
                val textEnd = matcher.end(1)

                spansToAdd.add(Triple(textStart, textEnd, StyleSpan(Typeface.BOLD)))
            }

            // Apply spans in reverse order to maintain indices
            for ((start, end, span) in spansToAdd.reversed()) {
                builder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun processItalicText(builder: SpannableStringBuilder) {
        val italicPatterns = arrayOf(
            Pattern.compile("\\*([^\\*]+)\\*"),  // *text*
            Pattern.compile("_([^_]+)_")         // _text_
        )

        for (pattern in italicPatterns) {
            val matcher = pattern.matcher(builder)
            val spansToAdd = mutableListOf<Triple<Int, Int, StyleSpan>>()

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                val textStart = matcher.start(1)
                val textEnd = matcher.end(1)

                spansToAdd.add(Triple(textStart, textEnd, StyleSpan(Typeface.ITALIC)))
            }

            // Apply spans in reverse order to maintain indices
            for ((start, end, span) in spansToAdd.reversed()) {
                builder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}