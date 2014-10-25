package de.lathspell.java_test_ee7_jpa.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static de.lathspell.java_test_ee7_jpa.model.Book.Status.IN_STOCK;

/**
 * Template for BookstoreXxxTest classes.
 *
 * Cannot be abstract as JUnit then complains.
 */
@Ignore
public abstract class BookstoreTemplate extends Template {

    protected Language langDe;

    protected Language langEn;

    protected Author authorTwain;

    protected Author authorVerne;

    protected Bookstore bookstoreBlack;

    @Test
    public void testCreate() {
        assertEquals(0L, ((Number) em.createNativeQuery("SELECT count(*) FROM books WHERE title = 'Huckleberry Finn'").getSingleResult()).longValue());

        Book book3 = new Book();
        book3.setAuthorId(authorTwain);
        book3.setTitle("Huckleberry Finn");
        book3.setLanguageId(langDe);
        book3.setPublishedIn(new Date());
        // book3.setStatus(); - Default via @PrePersist

        et.begin();
        assertNull(book3.getId());
        assertNull(book3.getStatus());

        // Persisting only tells JPA to treat the object as stored, it is not
        // necessarily INSERT'ed in the database.
        em.persist(book3);
        assertNull(book3.getId());
        assertEquals(IN_STOCK, book3.getStatus()); // is IN_STOCK due to @PrePersist!

        // Flushing the JPA cache will ensure that the INSERT is actually done.
        // Fields marked with @GeneratedValue like "id" are read with a SELECT.
        // Makes only sense inside transactions.
        em.flush();
        assertNotNull(book3.getId());
        assertEquals(IN_STOCK, book3.getStatus());

        // Only after a refresh(), values for @Column(insertable=false) attributes
        // which are generated by the database are loaded into the model object.
        em.refresh(book3);
        assertNotNull(book3.getId());
        assertEquals(IN_STOCK, book3.getStatus());

        et.commit();

        // verify
        Book book3b = (Book) em.createQuery("SELECT book FROM Book book WHERE book.title = 'Huckleberry Finn'", Book.class).getSingleResult();
        assertTrue(book3b.getId() >= 1);
        assertEquals("Mark", book3b.getAuthorId().getFirstName());
        assertEquals("Huckleberry Finn", book3b.getTitle());
        assertEquals(langDe, book3b.getLanguageId());
        assertEquals(IN_STOCK, book3b.getStatus());
    }

    @Test
    public void testRetrieve() {

        // Native SQL
        assertEquals(5L, ((Number) em.createNativeQuery("SELECT count(*) + 2 FROM books").getSingleResult()).longValue());

        // JSQL for List
        List<Author> authors = em.createQuery("SELECT a FROM Author a").getResultList();
        assertEquals(2, authors.size());

        // Named Query for List
        List<Author> authors2 = em.createNamedQuery("Author.findAll").getResultList();
        assertEquals(authors, authors2);

        // Native SQL for Object
        Book tomSawyerBook = em.createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class).setParameter("title", "Tom Sawyer").getSingleResult();
        assertNotNull(tomSawyerBook);
        assertTrue(tomSawyerBook.getId() > 0);
        assertEquals(langDe, tomSawyerBook.getLanguageId());
        assertEquals(IN_STOCK, tomSawyerBook.getStatus());

        // Named Query for Object
        Book tomSawyerBook2 = em.createNamedQuery("Book.findByTitle", Book.class).setParameter("title", "Tom Sawyer").getSingleResult();
        assertEquals(tomSawyerBook, tomSawyerBook2);

        // Criteria Query for Object (-> total crap, use QueryDSL!)
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> q = cb.createQuery(Book.class);
        Root<Book> c = q.from(Book.class);
        ParameterExpression<String> p1 = cb.parameter(String.class);
        q.select(c).where(cb.equal(c.get("title"), p1));
        TypedQuery<Book> query = em.createQuery(q);
        query.setParameter(p1, "Tom Sawyer");
        Book tomSawyerBook3 = query.getSingleResult();
        assertEquals(tomSawyerBook, tomSawyerBook3);

        // Referenced object (not loaded automatically!)
        List<BookToBookstore> b2bList = tomSawyerBook2.getBookToBookstoreList();
        assertNull(b2bList);
    }

    @Test
    public void testUpdateWrong() {
        assertEquals(0L, ((Number) em.createNativeQuery("SELECT count(*) FROM books WHERE title = 'Huckleberry Finn 2'").getSingleResult()).longValue());

        Book book = (Book) em.createQuery("SELECT book FROM Book book WHERE book.title = 'Tom Sawyer'").getSingleResult();
        book.setTitle("Huckleberry Finn 2");

        et.begin();
        // book is already in the Persistence Context as it was loaded using the
        // EntityManager. Although the following works, it is bad practice.
        em.persist(book);
        et.commit();

        assertEquals(1L, ((Number) em.createNativeQuery("SELECT count(*) FROM books WHERE title = 'Huckleberry Finn 2'").getSingleResult()).longValue());
    }

    @Test
    public void testUpdate() {
        assertEquals(0L, ((Number) em.createNativeQuery("SELECT count(*) FROM books WHERE title = 'Huckleberry Finn 2'").getSingleResult()).longValue());

        Book book = (Book) em.createQuery("SELECT book FROM Book book WHERE book.title = 'Tom Sawyer'").getSingleResult();

        // Persisting an entity only means that it is added to the EntityManagers
        // "Persistence Context" i.e. the group of objects that will be checked
        // for differences at commit time. Changes can be done afterwards as
        // well!
        // In this case book was automatically added to the Persistence Context by
        // loading it via the EntityManager.
        book.setTitle("Huckleberry Finn 2");

        et.begin();
        em.flush(); // requires active transaction
        et.commit();

        assertEquals(1L, ((Number) em.createNativeQuery("SELECT count(*) FROM books WHERE title = 'Huckleberry Finn 2'").getSingleResult()).longValue());
    }

    @Test
    public void testDelete() {
        assertEquals(1L, ((Number) em.createNativeQuery("SELECT count(*) FROM books WHERE title = 'Tom Sawyer'").getSingleResult()).longValue());

        Book book = (Book) em.createQuery("SELECT book FROM Book book WHERE book.title = 'Tom Sawyer'").getSingleResult();
        et.begin();
        // em.createNativeQuery("DELETE FROM book_to_bookstore WHERE book_id = ?").setParameter(1, book.getId()).executeUpdate();
        em.remove(book);
        et.commit();

        assertEquals(0L, ((Number) em.createNativeQuery("SELECT count(*) FROM books WHERE title = 'Tom Sawyer'").getSingleResult()).longValue());
    }

    @Test
    public void testCascadeOnDelete() throws SQLException {
        et.begin();

        Book b1 = (Book) em.createQuery("SELECT b FROM Book b WHERE b.title = :title")
                .setParameter("title", "Tom Sawyer")
                .getSingleResult();
        assertEquals("Tom Sawyer", b1.getTitle());

        // Remove entity
        em.remove(b1);

        // Check model
        List l = em.createQuery("SELECT b FROM Book b WHERE b.title = :title")
                .setParameter("title", "Tom Sawyer")
                .getResultList();
        assertEquals(0, l.size());

        // Check database (still within transaction)
        Connection conn = em.unwrap(Connection.class);
        PreparedStatement ps = conn.prepareStatement("SELECT title FROM books WHERE title = ?");
        ps.setString(1, "Tom Sawyer");
        ResultSet rs = ps.executeQuery();
        assertFalse(rs.next());

        et.commit();
    }

    @Test
    public void testCascadeOnDeleteFIXME() {
        et.begin();

        // Load all "book_to_bookstore" entries and associated "books" entries
        List<BookToBookstore> l1 = em.createQuery(""
                + "SELECT DISTINCT b2b "
                + "FROM BookToBookstore b2b "
                + "LEFT JOIN FETCH b2b.bookId b "
                + "WHERE b.title = :title")
                .setParameter("title", "Tom Sawyer")
                .getResultList();
        assertEquals(1, l1.size());
        BookToBookstore b2b1 = (BookToBookstore) l1.get(0);
        Book b1 = b2b1.getBookId();
        assertEquals("Tom Sawyer", b1.getTitle());
        int savedId = b1.getId();

        // Remove entity
        em.remove(b1);

        // Check model
        Book b2 = (Book) em.createQuery("SELECT b FROM Book b WHERE b.id = :id")
                .setParameter("id", savedId)
                .getSingleResult();
        assertNull(b2);

        et.commit();
    }

    @Test
    public void testJoinFetchFIXME() {
        et.begin();

        // Now the other way around
        List<Book> books2 = em.createQuery(""
                + "SELECT DISTINCT b "
                + "FROM Book b "
                + "LEFT JOIN FETCH b.bookToBookstoreList b2b "
                + "WHERE b.title = :title")
                .setParameter("title", "Tom Sawyer")
                .getResultList();
        assertEquals(1, books2.size());

        Book book2 = books2.get(0);
        assertEquals("Tom Sawyer", book2.getTitle());
        List<BookToBookstore> l2 = book2.getBookToBookstoreList();
        assertNotNull("Referenced b2b list is NULL!", l2); // FIXME: Why?

        et.commit();
    }

    @Override
    public void fixtures() {
        et.begin();

        em.createNativeQuery("DELETE FROM book_to_bookstore").executeUpdate();
        em.createNativeQuery("DELETE FROM bookstores").executeUpdate();
        em.createNativeQuery("DELETE FROM books").executeUpdate();
        em.createNativeQuery("DELETE FROM authors").executeUpdate();
        em.createNativeQuery("DELETE FROM languages").executeUpdate();

        langDe = new Language();
        langDe.setCode("de");
        langDe.setName("Deutsch");
        em.persist(langDe);

        langEn = new Language();
        langEn.setCode("en");
        langEn.setName("English");
        em.persist(langEn);

        authorTwain = new Author();
        authorTwain.setFirstName("Mark");
        authorTwain.setLastName("Twain");
        em.persist(authorTwain);

        authorVerne = new Author();
        authorVerne.setFirstName("Jules");
        authorVerne.setLastName("Verne");
        em.persist(authorVerne);

        Book book1 = new Book();
        book1.setAuthorId(authorTwain);
        book1.setLanguageId(langEn);
        book1.setStatus(IN_STOCK); // No default value!
        book1.setPublishedIn(new LocalDate().toDate());
        book1.setTitle("The Awful German Language");
        em.persist(book1);

        Book book2 = new Book();
        book2.setAuthorId(authorTwain);
        book2.setLanguageId(langDe);
        book2.setStatus(IN_STOCK);
        book2.setPublishedIn(new LocalDate(1950, 1, 1).toDate());
        book2.setTitle("Tom Sawyer");
        em.persist(book2);

        Book book3 = new Book();
        book3.setAuthorId(authorVerne);
        book3.setLanguageId(langDe);
        book3.setStatus(IN_STOCK);
        book3.setTitle("20.000 Meilen unter dem Meer");
        book3.setPublishedIn(new LocalDate(1950, 1, 1).toDate());
        em.persist(book3);

        bookstoreBlack = new Bookstore();
        bookstoreBlack.setName("MyBookstore");
        em.persist(bookstoreBlack);

        BookToBookstore b2b = new BookToBookstore();
        b2b.setBookId(book2);
        b2b.setBookstoreId(bookstoreBlack);
        b2b.setCreatedAt(new Date());
        b2b.setUpdatedAt(new Date());
        em.persist(b2b);

        b2b = new BookToBookstore();
        b2b.setBookId(book3);
        b2b.setBookstoreId(bookstoreBlack);
        b2b.setCreatedAt(new Date());
        b2b.setUpdatedAt(new Date());
        em.persist(b2b);

        et.commit();
    }

    /**
     * Some databases like Derby/JavaDB only return an Integer here.
     */
    @Test
    public void testReturnValue() {
        assertEquals(Long.class, em.createNativeQuery("SELECT count(*) FROM books").getSingleResult().getClass());
        // workaround: ((Number) em.createNativeQuery("SELECT count(*) FROM books").getSingleResult()).longValue()
    }
}
