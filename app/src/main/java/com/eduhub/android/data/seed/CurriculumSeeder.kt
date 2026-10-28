package com.eduhub.android.data.seed

import com.eduhub.android.data.local.EduHubDatabase
import com.eduhub.android.data.local.entities.*
import java.text.SimpleDateFormat
import java.util.*

object CurriculumSeeder {

    suspend fun populateDatabase(database: EduHubDatabase) {
        val courseDao = database.courseDao()
        val lessonDao = database.lessonDao()
        val quizDao = database.quizDao()
        val challengeDao = database.challengeDao()
        val userProgressDao = database.userProgressDao()
        val snippetDao = database.snippetDao()

        val courseCount = courseDao.getCourseCount()
        val lessonCount = lessonDao.getTotalLessonsCount()

        // 1. Check if already seeded with the complete 12-lesson curriculum (39 * 12 = 468)
        if (courseCount >= 39 && lessonCount >= 468) {
            return
        }

        // 2. Initialize default User Progress if missing
        if (userProgressDao.getUserProgress() == null) {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            userProgressDao.insertOrUpdate(
                UserProgressEntity(
                    id = 1,
                    studentName = "Future Tech Leader",
                    totalXp = 150,
                    currentStreak = 1,
                    lastActiveDate = todayStr,
                    completedLessonsCount = 0,
                    completedQuizzesCount = 0,
                    completedChallengesCount = 0,
                    isDarkMode = true,
                    speechRate = 1.0f,
                    preferredLanguageSlug = "python"
                )
            )
        }

        // 3. If upgrading from older 8-lesson curriculum, refresh lessons cleanly
        if (courseCount > 0 && lessonCount < 468) {
            lessonDao.deleteAllLessons()
            courseDao.updateAllCoursesTotalLessons(12)
            val existingCourses = courseDao.getAllCourses()
            for (course in existingCourses) {
                val spec = LanguageCatalogData.languages.find { it.slug == course.slug } ?: continue
                val lessons = create12Lessons(course.id, spec)
                lessonDao.insertAll(lessons)
            }
            return
        }

        // 4. Fresh Database Seeding for all 39 languages
        for (spec in LanguageCatalogData.languages) {
            val courseEntity = CourseEntity(
                slug = spec.slug,
                title = spec.courseTitle,
                languageName = spec.langName,
                category = spec.category,
                difficulty = spec.difficulty,
                totalHours = spec.hours,
                description = spec.description,
                paradigm = spec.paradigm,
                iconEmoji = spec.iconEmoji,
                totalLessons = 12,
                completedLessons = 0,
                progressPercentage = 0,
                isCompleted = false,
                isCustom = false,
                isFavorite = spec.slug in listOf("python", "kotlin", "javascript")
            )
            val courseId = courseDao.insertCourse(courseEntity)

            // Seed full 12-lesson progressive self-study curriculum (Tiers 0 to 3)
            val lessons = create12Lessons(courseId, spec)
            lessonDao.insertAll(lessons)

            // Seed Quiz & 5 Mastery Questions
            val quizId = quizDao.insertQuiz(
                QuizEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    title = "${spec.langName} Comprehensive Mastery Quiz",
                    totalQuestions = 5,
                    bestScore = 0,
                    isPassed = false
                )
            )

            val questions = listOf(
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 1,
                    questionText = "Which paradigm best characterizes ${spec.langName}?",
                    optionA = spec.paradigm.split(",").firstOrNull()?.trim() ?: "Object-Oriented",
                    optionB = "Pure Machine Binary Only",
                    optionC = "Unstructured Goto-only language",
                    optionD = "Static Batch-card punch system",
                    correctOptionIndex = 0,
                    explanation = "${spec.langName} is officially classified as: ${spec.paradigm}."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 2,
                    questionText = "What is the recommended approach for handling unexpected errors in ${spec.langName}?",
                    optionA = "Ignore errors and let the runtime crash",
                    optionB = "Use structured try/catch or Result/Option error propagation types",
                    optionC = "Write print statements only",
                    optionD = "Reboot the operating system",
                    correctOptionIndex = 1,
                    explanation = "Robust software uses structured exception handling or monadic Result/Option types to prevent crashes."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 3,
                    questionText = "How do higher-order collection transforms (like filter/map) improve code quality?",
                    optionA = "They make code slower and less readable",
                    optionB = "They enable declarative, readable, and less error-prone data transformations",
                    optionC = "They are only available in assembly languages",
                    optionD = "They eliminate the need for variables completely",
                    correctOptionIndex = 1,
                    explanation = "Declarative higher-order functions reduce boilerplate loops and prevent off-by-one indexing errors."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 4,
                    questionText = "What is the key benefit of immutability and const/val bindings?",
                    optionA = "It prevents unintended state mutation and enhances thread safety",
                    optionB = "It doubles memory usage without benefits",
                    optionC = "It disables functions",
                    optionD = "It prevents compilation",
                    correctOptionIndex = 0,
                    explanation = "Immutable data structures prevent side-effects and simplify concurrent and asynchronous execution."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 5,
                    questionText = "When completing a capstone project in ${spec.langName}, what is the primary architecture goal?",
                    optionA = "Putting all code into a single 10,000 line file",
                    optionB = "Separation of concerns, clean modularity, and reproducible tests",
                    optionC = "Avoiding comments and type definitions",
                    optionD = "Relying purely on global variables",
                    correctOptionIndex = 1,
                    explanation = "Enterprise capstone systems require modular architectures, clean abstractions, and testability."
                )
            )
            quizDao.insertAllQuestions(questions)

            // Seed Hands-On Coding Challenge
            challengeDao.insertChallenge(
                ChallengeEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    languageName = spec.langName,
                    title = spec.challengeTitle,
                    description = spec.challengeDescription,
                    starterCode = spec.challengeStarter,
                    rubricPatterns = spec.challengeRubric,
                    hint = spec.challengeHint,
                    solutionSnippet = spec.funcCode,
                    isSolved = false,
                    earnedXp = 100
                )
            )

            // Seed Snippet Starter
            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "${spec.langName} Quick Reference Starter",
                    languageSlug = spec.slug,
                    languageName = spec.langName,
                    code = spec.cheatSheetSummary,
                    tags = "syntax,cheatsheet,starter"
                )
            )
        }
    }

    private fun create12Lessons(courseId: Long, spec: LanguageSpec): List<LessonEntity> {
        return listOf(
            // TIER 0: FUNDAMENTALS & FIRST CODE (0% -> 25%)
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 1,
                moduleTitle = "Tier 0: Fundamentals & Core Syntax (0% to 25%)",
                lessonOrder = 1,
                title = "Platform Architecture, Tooling & First Executable Program",
                objectives = "Understand the language design philosophy, environment execution model, entry point semantics, and write your foundational first program.",
                contentMarkdown = buildLesson1Content(spec),
                codeExample = spec.helloCode,
                expectedOutput = "Program executed successfully with standard output verified.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 1,
                moduleTitle = "Tier 0: Fundamentals & Core Syntax (0% to 25%)",
                lessonOrder = 2,
                title = "Primitive Types, Memory Allocation & Immutability",
                objectives = "Master primitive types, explicit and inferred declarations, stack vs heap allocation, and value immutability.",
                contentMarkdown = buildLesson2Content(spec),
                codeExample = spec.varsCode,
                expectedOutput = "Variables inspected, typed, and bound in memory.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 1,
                moduleTitle = "Tier 0: Fundamentals & Core Syntax (0% to 25%)",
                lessonOrder = 3,
                title = "Arithmetic, Relational & Boolean Expressions",
                objectives = "Evaluate mathematical, relational, and boolean expressions with precision and proper operator precedence.",
                contentMarkdown = buildLesson3Content(spec),
                codeExample = spec.opsCode,
                expectedOutput = "Expression evaluation result verified.",
                isCompleted = false
            ),

            // TIER 1: CONTROL FLOW & ALGORITHMIC LOGIC (25% -> 50%)
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 2,
                moduleTitle = "Tier 1: Control Flow & Algorithmic Logic (25% to 50%)",
                lessonOrder = 4,
                title = "Conditional Logic, Branching & Pattern Matching",
                objectives = "Direct program flow using if/else structures, match guards, switch/when constructs, and pattern matching.",
                contentMarkdown = buildLesson4Content(spec),
                codeExample = spec.condCode,
                expectedOutput = "Decision branches evaluated.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 2,
                moduleTitle = "Tier 1: Control Flow & Algorithmic Logic (25% to 50%)",
                lessonOrder = 5,
                title = "Loops, Iterators & Sequence Processing",
                objectives = "Process repeated operations, sequences, and ranges with structured loops and collection iterators.",
                contentMarkdown = buildLesson5Content(spec),
                codeExample = spec.loopCode,
                expectedOutput = "Iteration sequence complete.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 2,
                moduleTitle = "Tier 1: Control Flow & Algorithmic Logic (25% to 50%)",
                lessonOrder = 6,
                title = "Functions, Scope & First-Class Closures",
                objectives = "Write modular, reusable functions, parameter passing semantics, return types, and lambda closures.",
                contentMarkdown = buildLesson6Content(spec),
                codeExample = spec.funcCode,
                expectedOutput = "Function pipeline executed.",
                isCompleted = false
            ),

            // TIER 2: DATA STRUCTURES & OOP ENGINEERING (50% -> 75%)
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 3,
                moduleTitle = "Tier 2: Data Structures & OOP Engineering (50% to 75%)",
                lessonOrder = 7,
                title = "Collections, Arrays & Higher-Order Transforms",
                objectives = "Structure and filter datasets with built-in collections and higher-order functional pipelines (map, filter, reduce).",
                contentMarkdown = buildLesson7Content(spec),
                codeExample = spec.collectionsCode,
                expectedOutput = "Collections mapped and filtered.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 3,
                moduleTitle = "Tier 2: Data Structures & OOP Engineering (50% to 75%)",
                lessonOrder = 8,
                title = "Object-Oriented Design, Classes & Encapsulation",
                objectives = "Construct robust domain models, encapsulate internal state with constructors and properties, and apply object-oriented design.",
                contentMarkdown = buildLesson8Content(spec),
                codeExample = spec.oopCode,
                expectedOutput = "Object instance created and state verified.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 3,
                moduleTitle = "Tier 2: Data Structures & OOP Engineering (50% to 75%)",
                lessonOrder = 9,
                title = "Structured Error Handling & Exception Safety",
                objectives = "Safeguard against runtime crashes using structured try/catch blocks, error types, and clean resource cleanup.",
                contentMarkdown = buildLesson9Content(spec),
                codeExample = spec.errorHandlingCode,
                expectedOutput = "Error handled safely without program crash.",
                isCompleted = false
            ),

            // TIER 3: ADVANCED SYSTEMS, CONCURRENCY & CAPSTONE (75% -> 100%)
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 4,
                moduleTitle = "Tier 3: Advanced Systems, Concurrency & Capstone (75% to 100%)",
                lessonOrder = 10,
                title = "Asynchronous Concurrency & Non-Blocking I/O",
                objectives = "Master asynchronous workflows, coroutines, non-blocking network/disk I/O, and concurrent task orchestration.",
                contentMarkdown = buildLesson10Content(spec),
                codeExample = spec.asyncIoCode,
                expectedOutput = "Concurrent asynchronous tasks completed.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 4,
                moduleTitle = "Tier 3: Advanced Systems, Concurrency & Capstone (75% to 100%)",
                lessonOrder = 11,
                title = "Data Serialization, Schema Mapping & File I/O",
                objectives = "Serialize and deserialize structured schemas, handle JSON formatting, and manage persistent data streams.",
                contentMarkdown = buildLesson11Content(spec),
                codeExample = spec.serializationCode,
                expectedOutput = "Data serialized and deserialized cleanly.",
                isCompleted = false
            ),
            LessonEntity(
                courseId = courseId,
                courseSlug = spec.slug,
                courseTitle = spec.courseTitle,
                moduleIndex = 4,
                moduleTitle = "Tier 3: Advanced Systems, Concurrency & Capstone (75% to 100%)",
                lessonOrder = 12,
                title = "Production Capstone: Enterprise System Architecture",
                objectives = "Construct a complete, production-ready enterprise software architecture integrating domain models, algorithmic logic, and error containment.",
                contentMarkdown = buildLesson12Content(spec),
                codeExample = spec.capstoneCode,
                expectedOutput = "Production capstone architecture compiled and executed.",
                isCompleted = false
            )
        )
    }

    private fun buildLesson1Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Welcome to **${spec.langName}**! In modern software development, ${spec.langName} is widely renowned for its **${spec.paradigm}** architecture. When code runs in ${spec.langName}, the runtime environment initializes stack memory for the execution thread, configures global symbols, and hooks up the standard I/O streams (`stdout`, `stderr`, `stdin`).

Writing your first executable program establishes the fundamental build-and-run workflow. Understanding entry point semantics and runtime initialization ensures you understand how programs bootstrap before executing business logic.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Entry Flow**: The instructions execute sequentially in the main entry scope.
2. **Standard Output**: String literals are evaluated and streamed directly to the console buffer.
3. **Clean Termination**: The host environment terminates with exit code 0 indicating success.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Syntax Delimiters**: Mismatched quotation marks or missing brackets cause immediate parse errors.
- **Environment Discrepancies**: Ensure compiler/runtime versions match your expected standard.
- **Ignoring Output Stream**: Always verify console outputs to confirm successful execution flow.

### 💡 Best Practices & Pro Tips
- Write self-documenting code with clear naming conventions and standard indentation.
- Keep the main entry point clean and delegate complex tasks to structured routines.
- Maintain readable console messages for better observability and debugging.

### 🧪 Hands-On Self-Study Challenge
**Task**: In the interactive code runner below, modify the output string to include your name, current year, and target learning milestone. Click **▶ Run Code** to verify!
""".trimIndent()

    private fun buildLesson2Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Data representation is the bedrock of computing. In **${spec.langName}**, every piece of information resides either in fast stack memory or managed heap memory. Primitive data types—such as integers, floating-point numbers, booleans, and character sequences—allow programs to model real-world state.

Understanding variable mutability vs immutability is essential. Immutable bindings (values that cannot be altered after declaration) prevent side-effects, simplify concurrent execution, and reduce unintended bugs.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Type Annotations & Inference**: Learn how the compiler determines variable types explicitly or through context.
2. **Memory Binding**: Values are bound to identifiers with defined lexical scopes and lifetimes.
3. **String Formatting**: Interpolate dynamic variables directly into human-readable messages.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Type Mismatches**: Assigning incompatible types without explicit conversion triggers type errors.
- **Unintended Mutation**: Modifying a shared variable when an immutable copy should have been created.
- **Variable Shadowing**: Accidentally redeclaring an identifier in an inner scope, hiding the outer value.

### 💡 Best Practices & Pro Tips
- Prefer immutable variable bindings (`val`, `const`, read-only) by default; only use mutable variables when mutation is strictly required.
- Use meaningful, descriptive identifier names that clearly convey purpose and intent.
- Be explicit with types on public interfaces and allow type inference on local variables.

### 🧪 Hands-On Self-Study Challenge
**Task**: Add two new variables representing a student's GPA and enrollment status, then print a formatted record string.
""".trimIndent()

    private fun buildLesson3Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Computational logic relies on arithmetic operators, relational comparisons, and boolean algebra. In **${spec.langName}**, expressions are evaluated according to strict operator precedence rules.

Understanding how operands are evaluated, how compound assignments function, and how short-circuit evaluation prevents unnecessary computation in boolean chains (`&&` and `||`) is crucial for building reliable algorithms.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Arithmetic Precedence**: Multiplication and division precede addition and subtraction.
2. **Relational Operators**: Values are compared producing boolean results (`true` or `false`).
3. **Compound Expressions**: Combining comparisons with logical operators to formulate complex rules.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Integer Truncation**: Performing division between two integers might drop decimals if not cast to floating-point.
- **Floating-Point Rounding**: Binary floating-point arithmetic can introduce subtle rounding inaccuracies.
- **Precedence Confusion**: Failing to group conditions with parentheses can cause logic branches to execute incorrectly.

### 💡 Best Practices & Pro Tips
- Always use parentheses to make mathematical and logical expression precedence unambiguous.
- Compare floating-point values using an epsilon threshold rather than exact equality (`==`).
- Keep conditional boolean expressions readable by breaking complex clauses into named boolean variables.

### 🧪 Hands-On Self-Study Challenge
**Task**: Compute the weighted average of three test scores (weights: 25%, 35%, 40%) and print whether the student qualified for honors (>= 85.0).
""".trimIndent()

    private fun buildLesson4Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Programs must make dynamic decisions based on runtime conditions. **${spec.langName}** provides control flow branching constructs such as `if`/`else` blocks, pattern matching, and switch/when constructs.

Modern CPU architectures use branch predictors to anticipate code execution paths. Writing clean, predictable branch conditions without deep nesting ensures optimal performance and maintainable logic.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Conditional Branching**: Code paths diverge based on evaluated boolean expressions.
2. **Pattern Matching & Guards**: Advanced syntax allows matching complex values and structures concisely.
3. **Early Returns**: Handling invalid conditions first (guard clauses) to flatten nested code structures.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Assignment vs Equality**: Accidentally using single equals (`=`) instead of double equals (`==`) in languages where allowed.
- **The Arrow Anti-Pattern**: Deeply nesting multiple levels of `if` statements makes code unreadable and bug-prone.
- **Missing Fallback**: Failing to provide a catch-all `else` or default case leading to unhandled edge cases.

### 💡 Best Practices & Pro Tips
- Invert conditions and return early to keep the main execution path at the root indentation level.
- Utilize exhaustive pattern matching when dealing with enums or sealed structures.
- Group related conditions into clean decision tables or lookup mappings.

### 🧪 Hands-On Self-Study Challenge
**Task**: Extend the ranking function in the editor to handle a new rank for scores above 1000 with a custom trophy icon, and test it!
""".trimIndent()

    private fun buildLesson5Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Iteration allows programs to process large sequences, repeat calculations, and transform data streams efficiently. **${spec.langName}** offers structured loops including counted `for` loops, iterator-driven traversals, and conditional `while` loops.

Understanding iteration mechanics—including loop invariants, iterator protocols, and loop control statements (`break`, `continue`)—is essential for algorithm design.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Range Traversal**: Looping through sequential intervals or numeric steps.
2. **Collection Enumeration**: Iterating over elements while accessing their indices cleanly.
3. **Loop Termination**: Ensuring termination conditions are guaranteed to occur to prevent infinite loops.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Infinite Loops**: Failing to advance counter variables or update termination conditions in `while` loops.
- **Off-By-One Errors**: Accessing indices from 0 to `N` instead of 0 to `N - 1`, leading to out-of-bounds errors.
- **Concurrent Modification**: Modifying a collection while actively iterating over it.

### 💡 Best Practices & Pro Tips
- Prefer iterator-based loops (`for item in items`) over manual index tracking.
- Keep loop bodies small and delegate complex per-item transformations to pure helper functions.
- Avoid placing expensive computations or memory allocations inside inner loops.

### 🧪 Hands-On Self-Study Challenge
**Task**: Write a loop that iterates through numbers 1 to 15, printing each number's square and tagging even numbers.
""".trimIndent()

    private fun buildLesson6Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Functions are the primary unit of modularity, code reuse, and abstraction in software engineering. In **${spec.langName}**, functions can be first-class citizens, meaning they can be passed as arguments, assigned to variables, and returned from other functions.

When a function executes, a new stack frame is created containing its local parameters and variables. Closures allow functions to capture and retain references to variables from their enclosing lexical scope even after that scope has executed.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Function Signatures**: Explicit parameter declarations, default values, and return type contracts.
2. **Lexical Scope**: Local variables exist only during function execution and cannot leak outwards.
3. **Higher-Order Closures**: Passing lambdas to configure customizable behavior.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Unintended Side-Effects**: Mutating external or global variables inside a function makes behavior unpredictable.
- **Missing Return Statements**: Forgetting to return a value in one branch of a function with multiple exit paths.
- **Excessive Parameters**: Passing more than 4-5 parameters indicates the function is doing too much or needs a parameter object.

### 💡 Best Practices & Pro Tips
- Adhere to the Single Responsibility Principle: each function should do one thing and do it well.
- Strive for pure functions (same inputs always produce same outputs with no side-effects) to make unit testing effortless.
- Document function contracts with clear docstrings or type signatures.

### 🧪 Hands-On Self-Study Challenge
**Task**: In the editor, implement a function `calculateDiscount(price, rate)` that validates inputs and returns the discounted total.
""".trimIndent()

    private fun buildLesson7Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Real-world systems process collections of data: user lists, sensor readings, transaction logs, and inventory items. **${spec.langName}** provides high-performance data structures such as dynamic arrays/lists, associative dictionaries/maps, and unique sets.

Modern software emphasizes declarative collection processing via functional pipelines: `map` (transform), `filter` (select), and `reduce` (aggregate). This replaces messy boilerplate loops with clean, readable data pipelines.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Collection Types**: Distinguish between indexed sequences (lists), unique sets, and key-value maps.
2. **Transform Pipelines**: Chain operations cleanly without intermediate mutable state.
3. **Complexity Awareness**: Understand that set/map lookups are typically O(1) compared to O(N) array scans.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Index Out of Bounds**: Accessing an index that does not exist in an array crashes the program.
- **Key Not Found**: Querying a dictionary for a non-existent key without checking or providing a fallback value.
- **Inefficient Searching**: Searching for items in a large list sequentially instead of utilizing a hash set or dictionary.

### 💡 Best Practices & Pro Tips
- Choose the right data structure: Sets for deduplication and fast membership checks; Maps for key lookups; Lists for ordered sequences.
- Use immutable collections whenever possible to ensure thread safety and prevent accidental modifications.
- Prefer functional collection methods (`filter`, `map`) over manual loop accumulation.

### 🧪 Hands-On Self-Study Challenge
**Task**: Take an array of test scores, filter out scores below 70, increase the remaining scores by 5 bonus points, and calculate the new average.
""".trimIndent()

    private fun buildLesson8Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Object-Oriented Programming (OOP) enables software architects to model real-world concepts as self-contained entities that bundle state (attributes) and behavior (methods). In **${spec.langName}**, classes serve as blueprints for instantiating objects.

The core pillar of OOP is **encapsulation**: restricting direct access to internal components and protecting data invariants. By exposing controlled methods and properties, objects guarantee they always remain in a valid, consistent state.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Class Definition & Constructors**: Initializing newly created instances with required parameters.
2. **Encapsulated State**: Keeping sensitive attributes private or internal to prevent external tampering.
3. **Methods & Computed Properties**: Exposing behavior that operates directly on the object's internal state.

### ⚠️ Common Pitfalls & Traps to Avoid
- **God Object Anti-Pattern**: Designing a single massive class that handles too many unrelated responsibilities.
- **Leaking Internal Mutability**: Returning direct references to internal mutable collections allows callers to bypass validation.
- **Overusing Deep Inheritance**: Deep inheritance trees create fragile, tightly coupled architectures. Prefer composition over inheritance.

### 💡 Best Practices & Pro Tips
- Keep fields private and expose minimal, purposeful public APIs.
- Validate constructor arguments immediately to prevent half-initialized objects.
- Favor composition (combining simple objects) over complex multi-level inheritance hierarchies.

### 🧪 Hands-On Self-Study Challenge
**Task**: In the editor, add a method `resetProgress()` to the CourseTracker class and verify that the progress percentage recalculates to 0.0%.
""".trimIndent()

    private fun buildLesson9Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
In production environments, unexpected errors will inevitably occur: network drops, malformed user input, missing files, or division by zero. Robust applications in **${spec.langName}** must handle errors gracefully without crashing the application.

Structured error handling uses `try`/`catch` blocks or monadic Result types to intercept exceptions, clean up open system resources, log helpful diagnostics, and return safe fallback states.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Try Block**: Encapsulates code that has a risk of failing.
2. **Catch / Except Handler**: Catches specific exception types and executes recovery logic.
3. **Finally / Ensure Cleanup**: Guaranteed to run regardless of success or failure, ideal for closing file streams and sockets.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Swallowing Exceptions**: Catching a generic exception and doing nothing (`catch (e) {}`) hides bugs and makes debugging impossible.
- **Using Exceptions for Control Flow**: Exceptions are expensive; use standard conditional checks for expected user inputs.
- **Failing to Release Resources**: Forgetting to close file handles, database connections, or sockets causes memory leaks.

### 💡 Best Practices & Pro Tips
- Catch specific, expected exception types rather than broad generic exception classes.
- Always log meaningful diagnostic context when an exception is caught.
- Utilize automatic resource management constructs (`using`, `with`, `try-with-resources`) to ensure deterministic cleanup.

### 🧪 Hands-On Self-Study Challenge
**Task**: Modify the error handling function in the editor to catch invalid negative input values and return a custom error message.
""".trimIndent()

    private fun buildLesson10Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Modern software interacts continuously with external systems: querying cloud databases, fetching REST APIs, and reading disk files. Synchronous execution blocks the main thread during these operations, freezing user interfaces and limiting throughput.

**${spec.langName}** provides asynchronous programming primitives (such as async/await, coroutines, promises, or channels). Non-blocking operations surrender execution back to the event loop or thread pool, allowing thousands of concurrent requests on minimal CPU threads.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Async Coroutines / Tasks**: Functions marked as asynchronous return future-resolved values.
2. **Await / Non-blocking Yield**: Pausing execution until a task resolves without stalling the host thread.
3. **Concurrent Fan-Out**: Launching multiple independent operations in parallel using `gather` or promise arrays.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Blocking the Event Loop**: Executing heavy CPU-bound loops inside an asynchronous thread blocks all concurrent tasks.
- **Unhandled Async Rejections**: Forgetting to catch errors on asynchronous operations can lead to silent crashes.
- **Race Conditions**: Two concurrent async tasks reading and writing shared mutable variables simultaneously without synchronization.

### 💡 Best Practices & Pro Tips
- Use async/await for all network, database, and file I/O operations.
- Always handle errors within async tasks to prevent unhandled rejection crashes.
- Parallelize independent async operations with concurrent combinators rather than awaiting them one-by-one sequentially.

### 🧪 Hands-On Self-Study Challenge
**Task**: In the code runner below, add a fourth asynchronous fetch task and execute all four tasks concurrently!
""".trimIndent()

    private fun buildLesson11Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Applications must exchange data across network boundaries and persist application state to disk. **${spec.langName}** relies on standardized data interchange formats, primarily JSON (JavaScript Object Notation), binary buffers, and schema serialization.

Serialization transforms in-memory data structures into a transportable string or byte stream. Deserialization reconstructs structured objects from raw streams, validating data schemas and types in the process.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Schema Definition**: Mapping domain properties to standardized key-value serialized representations.
2. **Encoding / Marshalling**: Transforming structured dictionaries, maps, or objects into JSON strings.
3. **Decoding / Parsing**: Parsing incoming JSON strings back into validated strongly-typed models.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Unescaped Special Characters**: Quotes, newlines, or backslashes in data can corrupt JSON payloads if not escaped properly.
- **Schema Evolution Mismatches**: Adding or renaming keys without handling backward compatibility breaks client applications.
- **Parsing Untrusted Payloads**: Failing to validate JSON structure before accessing deeply nested keys leads to runtime null pointer crashes.

### 💡 Best Practices & Pro Tips
- Always validate incoming serialized data against a defined schema before executing business logic.
- Keep serialization DTOs (Data Transfer Objects) decoupled from internal domain entities.
- Use standard, battle-tested serialization libraries rather than writing manual string concatenation.

### 🧪 Hands-On Self-Study Challenge
**Task**: Add a `student_id` and `completion_date` field to the serialized curriculum object and print the formatted JSON string.
""".trimIndent()

    private fun buildLesson12Content(spec: LanguageSpec): String = """
### 📖 Theoretical Foundation & Mental Model
Congratulations on reaching the final capstone module of **${spec.langName}**! Real-world software engineering combines all previously learned paradigms—primitive types, algorithmic logic, functions, data structures, OOP encapsulation, error containment, and serialization—into a cohesive enterprise system.

Production-grade system architecture prioritizes **separation of concerns**: dividing code into clear domain layers (Entities, Business Logic Services, Repository/Storage, and Presentation). This ensures systems are scalable, testable, and maintainable by distributed teams.

### 🔍 Code Mechanics & Syntax Breakdown
1. **Domain Modeling**: Encapsulating real-world state and validation rules inside cohesive classes.
2. **Data Aggregation**: Computing statistics, averages, and distinctions using clean algorithmic pipelines.
3. **Structured Reporting**: Exposing summary outputs that can be displayed on dashboards or exported to analytics systems.

### ⚠️ Common Pitfalls & Traps to Avoid
- **Spaghetti Architecture**: Mixing presentation formatting, data storage queries, and business calculations in one file.
- **Untested Edge Cases**: Failing to verify what happens when datasets are empty, resulting in division-by-zero errors.
- **Hardcoding Configuration**: Hardcoding credentials, endpoints, or constants inside core business logic.

### 💡 Best Practices & Pro Tips
- Design software with testability in mind: write small, decoupled modules that can be verified with unit tests.
- Maintain high code quality with meaningful comments, proper documentation, and consistent architectural patterns.
- Celebrate your mastery! You have completed the 0-to-Advanced journey and are now ready to tackle real-world engineering challenges.

### 🧪 Hands-On Self-Study Challenge
**Task**: In the capstone code below, add a method `getTopStudent()` that returns the highest scoring course or record. Run the complete capstone system to verify!
""".trimIndent()
}
