## JavaDoc Writing Standards

### 1. Objective

Generate consistent, professional, and meaningful JavaDoc for Java classes, methods, constructors, and fields when requested.

Follow the style demonstrated by `CorrelationIdFilter` in the `com.p2p.common.loggings` package.

JavaDoc must describe the actual implementation, responsibilities, behavior, and contract of the code. Do not invent functionality or document behavior that the implementation does not guarantee.

### 2. General Formatting Rules

- Use standard JavaDoc syntax: `/** ... */`.
- Write descriptions in English.
- Use complete, grammatically correct sentences.
- Start descriptive sentences with an uppercase letter and end them with a period.
- Use `<p>` to separate paragraphs when necessary.
- Use standard JavaDoc tags such as `@param`, `@return`, `@throws`, `@author`, `@version`, and `@since` where appropriate.
- Use standard JavaDoc formatting rather than Markdown formatting.
- Do not use Markdown bold syntax such as `**@param**`.
- Do not include unnecessary comments that merely repeat the method or variable name.
- Keep descriptions concise, technically accurate, and easy to understand.

### 3. Class-Level JavaDoc

Every class, interface, enum, or record should have a class-level JavaDoc when documentation is requested.

The description should explain:

1. The primary responsibility of the class.
2. Its important behavior or processing flow.
3. Relevant architectural or execution constraints, if applicable.

Use the following structure for class-level JavaDoc:

```java
/**
 * Briefly describes the primary responsibility of the class.
 *
 * <p>Explains the main behavior, processing flow, or important implementation details.
 *
 * <p>Describes relevant constraints or execution conditions when necessary.
 *
 * @author Ringuyen
 * @version 1.0.0
 * @since YYYY-MM-DD
 */
```

Apply the following metadata rules:

- **`@author`**: Always use `Ringuyen`. Do not replace it with another name.
- **`@version`**: Use `1.0.0` by default. Do not change this value when modifying, refactoring, or updating the code. Only the author may explicitly change the version.
- **`@since`**: Use the actual date when the file is first created or when its code is updated, formatted as `YYYY-MM-DD`.
  - For a newly created file, use the file creation date.
  - For an existing file whose code is modified, update `@since` to the date of that modification.
  - For documentation-only changes, update `@since` only if the author considers the change an update to the file under this convention.
  - Use the actual current date when generating or updating the file. Never invent or guess a date.
- Include all three tags in class-level JavaDoc.
- When updating existing code, preserve `@version` unless the author explicitly requests a version change.


### 4. Method-Level JavaDoc

Document public and protected methods when requested or when their behavior requires clarification.

Private methods should also be documented when they contain non-obvious logic, complex processing, or important implementation constraints.

Method descriptions should explain:

- What the method does.
- How it behaves in important scenarios.
- Relevant side effects or lifecycle behavior.
- Important constraints when applicable.

Use `@param` for method parameters, `@return` for non-void return values, and `@throws` for relevant exceptions.

Example:

```java
/**
 * Processes an incoming HTTP request and manages its correlation ID.
 *
 * <p>The correlation ID is retrieved from the request header or generated
 * if absent. It is then stored in the MDC and added to the response header.
 * The MDC entry is removed after request processing to prevent context
 * leakage between requests.
 *
 * @param request the incoming HTTP request
 * @param response the outgoing HTTP response
 * @param filterChain the filter chain
 * @throws ServletException if a Servlet-related error occurs
 * @throws IOException if an I/O error occurs
 */
```

Do not add `@return` to `void` methods.

Do not add `@param` to methods without parameters.

Do not document exceptions that the method neither declares nor meaningfully propagates unless they are relevant to its documented contract.

### 5. Override Methods

For overridden methods, follow these rules:

- Use `@Override` as required by Java.
- Add a JavaDoc comment when it clarifies the implementation-specific behavior or the method contract.
- Use `{@inheritDoc}` when inheriting the parent method's documentation is sufficient.
- Avoid repeating the entire parent documentation without adding useful information.

Example:

```java
/**
 * Returns the execution order of this filter.
 *
 * @return the highest precedence to execute this filter early
 */
@Override
public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
}
```

### 6. Field and Constant JavaDoc

Document fields and constants when their purpose, meaning, or constraints are not obvious.

Example:

```java
/**
 * Header name used to propagate the correlation ID between services.
 */
public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
```

For constants, explain their semantic meaning rather than merely repeating the constant name.

Avoid unnecessary JavaDoc for obvious local variables.

### 7. Exception Documentation

For each documented exception:

- Use the exact exception type.
- Explain the condition under which it can occur.
- Ensure the description is consistent with the actual implementation and method contract.

Example:

```java
/**
 * Retrieves a customer by its identifier.
 *
 * @param customerId the identifier of the customer
 * @return the customer associated with the specified identifier
 * @throws ResourceNotFoundException if no customer exists with the specified identifier
 */
```

Do not claim that an exception is thrown under a particular condition unless the implementation or its contract supports that statement.

### 8. Reactive Programming Documentation

For methods using Reactor types such as `Mono` and `Flux`:

- Describe the meaning of the returned publisher.
- Use `@return` to describe the expected completion, emitted value, or processing result.
- Document important reactive side effects, context propagation, cancellation behavior, or error propagation when relevant.
- Distinguish Reactor Context from thread-local storage such as MDC.
- Do not claim that a publisher has executed merely because it was created.
- Avoid blocking or subscription-related claims that are not supported by the implementation.

Example:

```java
/**
 * Filters the incoming request and propagates its correlation ID.
 *
 * <p>The correlation ID is extracted from the request header or generated
 * when missing. It is propagated to the response header, downstream request,
 * and Reactor context.
 *
 * @param exchange the current server web exchange
 * @param chain the web filter chain
 * @return a {@link Mono} that completes when request processing finishes
 */
```

### 9. Accuracy and Evidence Requirements

Before generating JavaDoc:

1. Read the complete method or class implementation.
2. Inspect relevant constants, parent classes, interfaces, and called methods when necessary.
3. Verify the actual execution order and side effects.
4. Confirm exception types and return semantics.
5. Distinguish synchronous behavior from asynchronous or reactive behavior.
6. Avoid unsupported claims about transactions, thread safety, security, performance, or distributed consistency.

If the implementation is insufficient to determine a behavior, do not invent an explanation. Use a narrower description or ask for clarification.

### 10. Scope of Changes

When asked to add or improve JavaDoc:

- Preserve the existing implementation.
- Do not modify business logic, method signatures, imports, annotations, or formatting unrelated to documentation.
- Do not rename classes, methods, variables, or constants.
- Do not add dependencies.
- Only change source code beyond JavaDoc when explicitly requested.

### 11. Expected Output

When asked to document a class, return the complete updated class unless the user requests only selected methods.

When asked to document multiple classes, apply the same standards consistently across all requested classes.

When asked to review existing JavaDoc, identify inaccurate, redundant, incomplete, or non-standard documentation and provide corrected JavaDoc.

When the user requests JavaDoc only, do not provide lengthy explanations outside the code.

### 12. Final Quality Checklist

Before returning the result, verify:

- [ ] Class-level JavaDoc describes the actual responsibility.
- [ ] Method-level JavaDoc describes actual behavior.
- [ ] All relevant parameters are documented.
- [ ] Non-void return values are documented.
- [ ] Relevant declared exceptions are documented accurately.
- [ ] JavaDoc tags use standard syntax.
- [ ] English grammar and punctuation are correct.
- [ ] No unsupported assumptions or fabricated metadata are included.
- [ ] No business logic has been changed.
- [ ] Documentation follows the project's existing conventions.

**Principle:** JavaDoc must explain the code's real behavior and contract, not what the AI assumes the code should do.
