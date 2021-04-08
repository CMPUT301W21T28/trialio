package com.example.trialio;

import com.example.trialio.models.Location;
import com.example.trialio.models.Question;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test for Question class
 */
public class QuestionTest {

    /**
     * Test constructors for questions
     */
    @Test
    void testCreateNonNegTrial() {
        Question q = new Question();
        assertEquals(Question.class, q.getClass());
        assertNull(q.getTitle());

        Question q2 = new Question("AU4T811G", "I love chicken nuggets.", "Ryan", "Why are chicken nuggets so tasty?");
        assertEquals(q2.getTitle(), "Why are chicken nuggets so tasty?");
    }
}
