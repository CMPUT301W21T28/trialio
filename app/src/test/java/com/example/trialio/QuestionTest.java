package com.example.trialio;

import com.example.trialio.models.Question;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for Question class
 */
public class QuestionTest {

    /**
     * Test constructors for questions
     */
    @Test
    void testCreateQuestion() {
        Question q = new Question();

        assertEquals(Question.class, q.getClass());
        assertNull(q.getTitle());

        Question q2 = new Question("AU4T811G", "I love chicken nuggets.", "Ryan", "Why are chicken nuggets so tasty?");

        assertEquals(Question.class, q2.getClass());
        assertEquals(q2.getTitle(), "Why are chicken nuggets so tasty?");
    }
}
