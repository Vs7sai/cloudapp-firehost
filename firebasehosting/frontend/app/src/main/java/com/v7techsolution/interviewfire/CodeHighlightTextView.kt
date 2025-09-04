package com.v7techsolution.interviewfire

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.regex.Pattern

class CodeHighlightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    // VS Code Dark Theme Colors
    private val backgroundColor = Color.parseColor("#1e1e1e")
    private val keywordColor = Color.parseColor("#569cd6")
    private val stringColor = Color.parseColor("#ce9178")
    private val commentColor = Color.parseColor("#6a9955")
    private val numberColor = Color.parseColor("#b5cea8")
    private val functionColor = Color.parseColor("#dcdcaa")
    private val typeColor = Color.parseColor("#4ec9b0")
    private val operatorColor = Color.parseColor("#d4d4d4")
    private val bracketColor = Color.parseColor("#ffd700")

    // Programming language keywords
    private val keywords = setOf(
        // Java/Kotlin keywords
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
        "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
        "throw", "throws", "transient", "try", "void", "volatile", "while",
        // Kotlin specific
        "val", "var", "fun", "object", "companion", "data", "sealed", "inner", "lateinit",
        "override", "open", "internal", "infix", "operator", "inline", "suspend", "tailrec",
        "when", "is", "as", "by", "constructor", "init", "property", "field", "receiver",
        "param", "setparam", "delegate",
        // JavaScript/Node.js
        "function", "const", "let", "var", "console", "log", "require", "module", "exports",
        "async", "await", "Promise", "then", "catch", "finally", "try", "throw",
        // Python
        "def", "class", "if", "elif", "else", "for", "while", "import", "from", "as", "with",
        "try", "except", "finally", "raise", "return", "yield", "lambda", "and", "or", "not",
        "True", "False", "None", "self", "super",
        // Docker
        "FROM", "RUN", "COPY", "ADD", "WORKDIR", "EXPOSE", "ENV", "ARG", "LABEL", "USER",
        "VOLUME", "ENTRYPOINT", "CMD", "ONBUILD", "STOPSIGNAL", "HEALTHCHECK", "SHELL",
        // Kubernetes
        "apiVersion", "kind", "metadata", "spec", "template", "containers", "ports", "env",
        "volumeMounts", "volumes", "selector", "replicas", "strategy", "service", "deployment",
        // AWS/Terraform
        "resource", "provider", "variable", "output", "module", "data", "terraform", "aws"
    )

    init {
        // Set background color for code blocks
        setBackgroundColor(backgroundColor)
        setTextColor(Color.parseColor("#d4d4d4")) // Default text color
        setTypeface(Typeface.MONOSPACE) // Monospace font for code
        setPadding(24, 16, 24, 16) // Code block padding
    }

    fun setCodeText(text: String, language: String = "kotlin") {
        val highlightedText = highlightSyntax(text, language)
        setText(highlightedText)
    }

    private fun highlightSyntax(text: String, language: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder(text)

        when (language.lowercase()) {
            "kotlin", "java" -> highlightKotlinJava(builder, text)
            "javascript", "js", "node" -> highlightJavaScript(builder, text)
            "python", "py" -> highlightPython(builder, text)
            "docker", "dockerfile" -> highlightDocker(builder, text)
            "yaml", "yml" -> highlightYaml(builder, text)
            "bash", "shell", "sh", "command" -> highlightBash(builder, text)
            else -> highlightGeneric(builder, text)
        }

        return builder
    }

    private fun highlightKotlinJava(builder: SpannableStringBuilder, text: String) {
        // Highlight keywords
        for (keyword in keywords) {
            val pattern = Pattern.compile("\\b$keyword\\b")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                builder.setSpan(
                    ForegroundColorSpan(keywordColor),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setSpan(
                    StyleSpan(Typeface.BOLD),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        // Highlight strings
        highlightPattern(builder, text, "\"([^\"\\\\]|\\\\.)*\"", stringColor)
        highlightPattern(builder, text, "'([^'\\\\]|\\\\.)*'", stringColor)

        // Highlight comments
        highlightPattern(builder, text, "//.*$", commentColor)
        highlightPattern(builder, text, "/\\*.*?\\*/", commentColor, true)

        // Highlight numbers
        highlightPattern(builder, text, "\\b\\d+\\.?\\d*\\b", numberColor)

        // Highlight brackets and operators
        highlightPattern(builder, text, "[{}()\\[\\]]", bracketColor)
        highlightPattern(builder, text, "[+\\-*/=<>!&|^%~?:;.,]", operatorColor)
    }

    private fun highlightJavaScript(builder: SpannableStringBuilder, text: String) {
        highlightKotlinJava(builder, text) // Similar to Java/Kotlin

        // Additional JS specific patterns
        highlightPattern(builder, text, "\\b(console|log|error|warn|info)\\b", functionColor)
        highlightPattern(builder, text, "\\b(require|module|exports)\\b", keywordColor)
    }

    private fun highlightPython(builder: SpannableStringBuilder, text: String) {
        // Highlight keywords
        for (keyword in keywords) {
            val pattern = Pattern.compile("\\b$keyword\\b")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                builder.setSpan(
                    ForegroundColorSpan(keywordColor),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        // Highlight strings
        highlightPattern(builder, text, "\"([^\"\\\\]|\\\\.)*\"", stringColor)
        highlightPattern(builder, text, "'([^'\\\\]|\\\\.)*'", stringColor)
        highlightPattern(builder, text, "\"\"\".*?\"\"\"", stringColor, true)
        highlightPattern(builder, text, "'''(.*?)'''", stringColor, true)

        // Highlight comments
        highlightPattern(builder, text, "#.*$", commentColor)

        // Highlight numbers
        highlightPattern(builder, text, "\\b\\d+\\.?\\d*\\b", numberColor)

        // Highlight operators
        highlightPattern(builder, text, "[+\\-*/=<>!&|^%~?:;.,]", operatorColor)
    }

    private fun highlightDocker(builder: SpannableStringBuilder, text: String) {
        // Highlight Docker instructions
        for (keyword in keywords) {
            if (keyword.length > 2) { // Only longer keywords for Docker
                val pattern = Pattern.compile("^\\s*$keyword\\b", Pattern.MULTILINE)
                val matcher = pattern.matcher(text)
                while (matcher.find()) {
                    builder.setSpan(
                        ForegroundColorSpan(keywordColor),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    builder.setSpan(
                        StyleSpan(Typeface.BOLD),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        // Highlight strings
        highlightPattern(builder, text, "\"([^\"\\\\]|\\\\.)*\"", stringColor)

        // Highlight comments
        highlightPattern(builder, text, "#.*$", commentColor)
    }

    private fun highlightYaml(builder: SpannableStringBuilder, text: String) {
        // Highlight keys
        val keyPattern = Pattern.compile("^(\\s*)[^\\s#:][^#:]*:", Pattern.MULTILINE)
        val keyMatcher = keyPattern.matcher(text)
        while (keyMatcher.find()) {
            builder.setSpan(
                ForegroundColorSpan(functionColor),
                keyMatcher.start(),
                keyMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Highlight values
        highlightPattern(builder, text, ":\\s*\"([^\"\\\\]|\\\\.)*\"", stringColor)
        highlightPattern(builder, text, ":\\s*'([^'\\\\]|\\\\.)*'", stringColor)
        highlightPattern(builder, text, ":\\s*\\d+\\.?\\d*", numberColor)

        // Highlight comments
        highlightPattern(builder, text, "#.*$", commentColor)
    }

    private fun highlightBash(builder: SpannableStringBuilder, text: String) {
        // Highlight commands
        val commands = setOf(
            "ls", "cd", "pwd", "mkdir", "rm", "cp", "mv", "cat", "grep", "find", "chmod",
            "chown", "ps", "kill", "top", "df", "du", "tar", "gzip", "curl", "wget", "ssh",
            "scp", "rsync", "docker", "kubectl", "aws", "terraform", "git", "npm", "yarn",
            "python", "java", "javac", "gradle", "./gradlew", "adb"
        )

        for (command in commands) {
            val pattern = Pattern.compile("\\b$command\\b")
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                builder.setSpan(
                    ForegroundColorSpan(functionColor),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setSpan(
                    StyleSpan(Typeface.BOLD),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        // Highlight strings
        highlightPattern(builder, text, "\"([^\"\\\\]|\\\\.)*\"", stringColor)
        highlightPattern(builder, text, "'([^'\\\\]|\\\\.)*'", stringColor)

        // Highlight comments
        highlightPattern(builder, text, "#.*$", commentColor)

        // Highlight operators and special characters
        highlightPattern(builder, text, "[|&;><]", operatorColor)
        highlightPattern(builder, text, "\\$[A-Za-z_][A-Za-z0-9_]*", typeColor) // Variables
    }

    private fun highlightGeneric(builder: SpannableStringBuilder, text: String) {
        // Basic highlighting for unknown languages
        highlightPattern(builder, text, "\"([^\"\\\\]|\\\\.)*\"", stringColor)
        highlightPattern(builder, text, "'([^'\\\\]|\\\\.)*'", stringColor)
        highlightPattern(builder, text, "//.*$", commentColor)
        highlightPattern(builder, text, "#.*$", commentColor)
        highlightPattern(builder, text, "/\\*.*?\\*/", commentColor, true)
        highlightPattern(builder, text, "\\b\\d+\\.?\\d*\\b", numberColor)
    }

    private fun highlightPattern(
        builder: SpannableStringBuilder,
        text: String,
        regex: String,
        color: Int,
        multiline: Boolean = false
    ) {
        val flags = if (multiline) Pattern.MULTILINE or Pattern.DOTALL else 0
        val pattern = Pattern.compile(regex, flags)
        val matcher = pattern.matcher(text)

        while (matcher.find()) {
            builder.setSpan(
                ForegroundColorSpan(color),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    // Convenience methods for different languages
    fun setKotlinCode(code: String) = setCodeText(code, "kotlin")
    fun setJavaCode(code: String) = setCodeText(code, "java")
    fun setJavaScriptCode(code: String) = setCodeText(code, "javascript")
    fun setPythonCode(code: String) = setCodeText(code, "python")
    fun setDockerCode(code: String) = setCodeText(code, "docker")
    fun setYamlCode(code: String) = setCodeText(code, "yaml")
    fun setBashCode(code: String) = setCodeText(code, "bash")
    fun setCommandCode(code: String) = setCodeText(code, "command")
}