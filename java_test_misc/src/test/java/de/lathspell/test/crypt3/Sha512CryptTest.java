package de.lathspell.test.crypt3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class Sha512CryptTest {

    @Test
    public void testSha512CryptStrings() throws Exception {
        // empty data
        assertEquals("$6$foo$Nywkte7LPWjaJhWjNeGJN.dFdY3pN1wYlGifyRLYOVlGS9EMSiZaDDe/BGSOYQ327q9.32I4UqQ5odsqvsBLX/", Crypt.crypt("", "$6$foo"));
        // salt gets cut at dollar sign
        assertEquals("$6$45678$f2en/Y053Knir/wu/T8DQKSbiUGcPcbXKsmyVlP820dIpXoY0KlqgUqRVFfavdRXwDMUZYsxPOymA4zgX0qE5.", Crypt.crypt("secret", "$6$45678"));
        assertEquals("$6$45678$f2en/Y053Knir/wu/T8DQKSbiUGcPcbXKsmyVlP820dIpXoY0KlqgUqRVFfavdRXwDMUZYsxPOymA4zgX0qE5.", Crypt.crypt("secret", "$6$45678$012"));
        assertEquals("$6$45678$f2en/Y053Knir/wu/T8DQKSbiUGcPcbXKsmyVlP820dIpXoY0KlqgUqRVFfavdRXwDMUZYsxPOymA4zgX0qE5.", Crypt.crypt("secret", "$6$45678$012$456"));
        // salt gets cut at maximum length
        assertEquals("$6$1234567890123456$d2HCAnimIF5VMqUnwaZ/4JhNDJ.ttsjm0nbbmc9eE7xUYiw79GMvXUc5ZqG5BlqkXSbASZxrvR0QefAgdLbeH.", Crypt.crypt("secret", "$6$1234567890123456"));
        assertEquals("$6$1234567890123456$d2HCAnimIF5VMqUnwaZ/4JhNDJ.ttsjm0nbbmc9eE7xUYiw79GMvXUc5ZqG5BlqkXSbASZxrvR0QefAgdLbeH.", Crypt.crypt("secret", "$6$1234567890123456789"));
    }

    @Test
    public void testSha512CryptBytes() throws Exception {
        // An empty Bytearray equals an empty String
        assertEquals("$6$foo$Nywkte7LPWjaJhWjNeGJN.dFdY3pN1wYlGifyRLYOVlGS9EMSiZaDDe/BGSOYQ327q9.32I4UqQ5odsqvsBLX/", Crypt.crypt(new byte[0], "$6$foo"));
        // UTF-8 stores \u00e4 "a with diaeresis" as two bytes 0xc3 0xa4.
        assertEquals("$6$./$fKtWqslQkwI8ZxjdWoeS.jHHrte97bZxiwB5gwCRHX6LG62fUhT6Bb5MRrjWvieh0C/gxh8ItFuTsVy80VrED1", Crypt.crypt("t\u00e4st", "$6$./$"));
        // ISO-8859-1 stores "a with diaeresis" as single byte 0xe4.
        assertEquals("$6$./$L49DSK.d2df/LxGLJQMyS5A/Um.TdHqgc46j5FpScEPlqQHP5dEazltaDNDZ6UEs2mmNI6kPwtH/rsP9g5zBI.", Crypt.crypt("t\u00e4st".getBytes("ISO-8859-1"), "$6$./$"));
    }

    @Test
    public void testSha512ExplicitCall() throws Exception {
        assertTrue(Sha2Crypt.sha512Crypt("secret".getBytes()).matches("^\\$6\\$[a-zA-Z0-9./]{0,16}\\$.{1,}$"));
        assertTrue(Sha2Crypt.sha512Crypt("secret".getBytes(), null).matches("^\\$6\\$[a-zA-Z0-9./]{0,16}\\$.{1,}$"));
    }
}

