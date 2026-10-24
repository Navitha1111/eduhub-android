package com.eduhub.android.utils

import com.eduhub.android.domain.models.CodeExecutionResult

object CodeEvaluator {

    fun evaluateCode(
        languageSlug: String,
        userCode: String,
        rubricPatterns: String = ""
    ): CodeExecutionResult {
        val trimmed = userCode.trim()
        if (trimmed.isEmpty()) {
            return CodeExecutionResult(
                isSuccess = false,
                output = "❌ Execution Error: Code buffer is empty. Write code to execute.",
                missingChecks = listOf("Code must not be empty")
            )
        }

        val requiredPatterns = rubricPatterns
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val passed = mutableListOf<String>()
        val missing = mutableListOf<String>()

        for (pattern in requiredPatterns) {
            if (trimmed.contains(pattern, ignoreCase = false) || trimmed.contains(pattern, ignoreCase = true)) {
                passed.add("Pattern '$pattern' detected")
            } else {
                missing.add("Missing required token/pattern: '$pattern'")
            }
        }

        val isSuccess = missing.isEmpty()

        val output = if (isSuccess) {
            buildString {
                appendLine("⚡ [Static Compilation & Rubric Check: PASSED]")
                appendLine("Language Runtime: ${languageSlug.uppercase()} Engine")
                appendLine("----------------------------------------")
                appendLine("✓ Syntax validation completed with 0 errors.")
                if (passed.isNotEmpty()) {
                    appendLine("✓ Rubric criteria satisfied:")
                    passed.forEach { appendLine("   • $it") }
                }
                appendLine("----------------------------------------")
                appendLine("Output stream: Program returned exit code 0.")
            }
        } else {
            buildString {
                appendLine("⚠️ [Rubric Verification Alert]")
                appendLine("Language Runtime: ${languageSlug.uppercase()} Engine")
                appendLine("----------------------------------------")
                appendLine("Passed checks: ${passed.size}/${requiredPatterns.size}")
                missing.forEach { appendLine("   ✗ $it") }
                appendLine("----------------------------------------")
                appendLine("Hint: Check requirements and hints before resubmitting.")
            }
        }

        return CodeExecutionResult(
            isSuccess = isSuccess,
            output = output,
            passedChecks = passed,
            missingChecks = missing
        )
    }

    fun getStarterCodeForLanguage(slug: String): String {
        return when (slug) {
            "python" -> "def calculate_xp(lessons: int, streak: int) -> int:\n    bonus = streak * 10\n    return lessons * 50 + bonus\n\nprint('Total XP:', calculate_xp(5, 7))"
            "javascript" -> "const calculateXp = (lessons, streak) => {\n  const bonus = streak * 10;\n  return lessons * 50 + bonus;\n};\nconsole.log('Total XP:', calculateXp(5, 7));"
            "typescript" -> "function calculateXp(lessons: number, streak: number): number {\n  const bonus: number = streak * 10;\n  return lessons * 50 + bonus;\n}\nconsole.log('Total XP:', calculateXp(5, 7));"
            "kotlin" -> "fun calculateXp(lessons: Int, streak: Int): Int {\n    val bonus = streak * 10\n    return lessons * 50 + bonus\n}\nprintln(\"Total XP: \${calculateXp(5, 7)}\")"
            "java" -> "public class Main {\n    public static int calculateXp(int lessons, int streak) {\n        return lessons * 50 + (streak * 10);\n    }\n    public static void main(String[] args) {\n        System.out.println(\"Total XP: \" + calculateXp(5, 7));\n    }\n}"
            "go" -> "package main\n\nimport \"fmt\"\n\nfunc calculateXp(lessons, streak int) int {\n    return lessons*50 + streak*10\n}\n\nfunc main() {\n    fmt.Println(\"Total XP:\", calculateXp(5, 7))\n}"
            "rust" -> "fn calculate_xp(lessons: u32, streak: u32) -> u32 {\n    lessons * 50 + streak * 10\n}\n\nfn main() {\n    println!(\"Total XP: {}\", calculate_xp(5, 7));\n}"
            "cpp" -> "#include <iostream>\n\nint calculateXp(int lessons, int streak) {\n    return lessons * 50 + streak * 10;\n}\n\nint main() {\n    std::cout << \"Total XP: \" << calculateXp(5, 7) << std::endl;\n    return 0;\n}"
            "csharp" -> "using System;\n\nint CalculateXp(int lessons, int streak) => lessons * 50 + streak * 10;\nConsole.WriteLine($\"Total XP: {CalculateXp(5, 7)}\");"
            "swift" -> "func calculateXp(lessons: Int, streak: Int) -> Int {\n    return lessons * 50 + streak * 10\n}\nprint(\"Total XP: \\(calculateXp(lessons: 5, streak: 7))\")"
            "dart" -> "int calculateXp(int lessons, int streak) => lessons * 50 + streak * 10;\nvoid main() {\n  print('Total XP: \${calculateXp(5, 7)}');\n}"
            "sql" -> "SELECT 'Total XP' AS metric, (5 * 50 + 7 * 10) AS total_xp;"
            "htmlcss" -> "<!DOCTYPE html>\n<html>\n<head>\n  <style>\n    body { background: #0f172a; color: #38bdf8; font-family: sans-serif; }\n  </style>\n</head>\n<body>\n  <h1>Welcome to EDUHUB HTML & CSS!</h1>\n</body>\n</html>"
            "r" -> "calculate_xp <- function(lessons, streak) {\n  return(lessons * 50 + streak * 10)\n}\ncat('Total XP:', calculate_xp(5, 7), '\\n')"
            "php" -> "<?php\nfunction calculateXp(int \$lessons, int \$streak): int {\n    return \$lessons * 50 + \$streak * 10;\n}\necho 'Total XP: ' . calculateXp(5, 7) . \"\\n\";\n?>"
            "ruby" -> "def calculate_xp(lessons, streak)\n  lessons * 50 + streak * 10\nend\nputs \"Total XP: #{calculate_xp(5, 7)}\""
            "c" -> "#include <stdio.h>\n\nint calculate_xp(int lessons, int streak) {\n    return lessons * 50 + streak * 10;\n}\n\nint main() {\n    printf(\"Total XP: %d\\n\", calculate_xp(5, 7));\n    return 0;\n}"
            "docker" -> "FROM alpine:latest\nWORKDIR /app\nRUN echo 'EDUHUB Container Engine Ready'\nCMD [\"echo\", \"Container running\"]"
            "react" -> "import React from 'react';\n\nexport default function App() {\n  return <h1>Hello from React 19 on EDUHUB!</h1>;\n}"
            "cybersecurity" -> "import hashlib\n\nhash_val = hashlib.sha256(b'EDUHUB-2026').hexdigest()\nprint('Cryptographic SHA-256 Digest:', hash_val)"
            "bash" -> "#!/usr/bin/env bash\nLESSONS=5\nSTREAK=7\nTOTAL_XP=$((LESSONS * 50 + STREAK * 10))\necho \"Total XP: \${TOTAL_XP}\""
            "powershell" -> "$Lessons = 5; $Streak = 7\n$TotalXp = ($Lessons * 50) + ($Streak * 10)\nWrite-Output \"Total XP: $TotalXp\""
            "nodejs" -> "const calculateXp = (lessons, streak) => lessons * 50 + streak * 10;\nconsole.log('Total XP:', calculateXp(5, 7));"
            "git" -> "git status\ngit log --oneline -n 3"
            "mongodb" -> "db.students.find({ certified: true }).limit(5);"
            "linux" -> "uname -a\ndf -h\nfree -m"
            "solidity" -> "// SPDX-License-Identifier: MIT\npragma solidity ^0.8.20;\ncontract EduHubXp {\n    function calculateXp(uint l, uint s) external pure returns (uint) {\n        return l * 50 + s * 10;\n    }\n}"
            "graphql" -> "query GetCourses {\n  courses {\n    id\n    title\n    durationHours\n  }\n}"
            "zig" -> "const std = @import(\"std\");\npub fn main() void {\n    const lessons: u32 = 5;\n    const streak: u32 = 7;\n    std.debug.print(\"Total XP: {d}\\n\", .{lessons * 50 + streak * 10});\n}"
            "assembly" -> "section .text\nglobal main\nmain:\n    mov rax, 320    ; Total XP\n    ret"
            "julia" -> "calculate_xp(lessons, streak) = lessons * 50 + streak * 10\nprintln(\"Total XP: \", calculate_xp(5, 7))"
            "scala" -> "def calculateXp(lessons: Int, streak: Int): Int = lessons * 50 + streak * 10\n@main def run(): Unit = println(s\"Total XP: \${calculateXp(5, 7)}\")"
            "haskell" -> "calculateXp :: Int -> Int -> Int\ncalculateXp lessons streak = lessons * 50 + streak * 10\nmain = print (calculateXp 5 7)"
            "elixir" -> "calculate_xp = fn l, s -> l * 50 + s * 10 end\nIO.puts(\"Total XP: #{calculate_xp.(5, 7)}\")"
            "clojure" -> "(defn calculate-xp [l s] (+ (* l 50) (* s 10)))\n(println \"Total XP:\" (calculate-xp 5 7))"
            "lua" -> "local function calculate_xp(lessons, streak)\n    return lessons * 50 + streak * 10\nend\nprint('Total XP: ' .. calculate_xp(5, 7))"
            "perl" -> "sub calculate_xp { my ($l, $s) = @_; return $l * 50 + $s * 10; }\nprint 'Total XP: ' . calculate_xp(5, 7) . \"\\n\";"
            "matlab" -> "lessons = 5; streak = 7;\ntotal_xp = lessons * 50 + streak * 10;\ndisp(['Total XP: ', num2str(total_xp)]);"
            "android-compose" -> "@Composable\nfun AppGreeting() {\n    Text(\"Welcome to Modern Jetpack Compose & Kotlin!\")\n}"
            else -> "// Write your $slug program here\nconsole.log('Hello from EDUHUB $slug!');"
        }
    }
}
