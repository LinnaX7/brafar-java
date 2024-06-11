package comp.assignment2;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class SpecialNumberTest {
    @Test(timeout = 2000)
    public void test1() {
        assertTrue(SpecialNumber.isSpecial(30));

    }

    @Test(timeout = 2000)
    public void test2() {
        assertFalse(SpecialNumber.isSpecial(210));

    }

    @Test(timeout = 2000)
    public void test3() {
        assertFalse(SpecialNumber.isSpecial(4));

    }

    @Test(timeout = 200)
    public void test4() {
        assertFalse(SpecialNumber.isSpecial(-30));

    }

    @Test(timeout = 2000)
    public void test5() {
        assertFalse(SpecialNumber.isSpecial(0));

    }

}

