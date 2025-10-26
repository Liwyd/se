const readlineSync = require('readline-sync');

class BookManager {
    constructor() {
        this.books = [];
        this.nextId = 1;
    }

    addBook(employee) {
        console.log('\n--- ثبت کتاب جدید ---');
        const title = readlineSync.question('عنوان کتاب: ');
        const author = readlineSync.question('نام نویسنده: ');
        const publicationYear = readlineSync.question('سال نشر: ');
        const isbn = readlineSync.question('شابک (ISBN): ');
        const category = readlineSync.question('دسته‌بندی: ');

        const book = {
            id: this.nextId++,
            title: title,
            author: author,
            publicationYear: parseInt(publicationYear),
            isbn: isbn,
            category: category,
            isAvailable: true,
            addedBy: employee.id,
            addedDate: new Date()
        };

        this.books.push(book);
        console.log('کتاب با موفقیت ثبت شد!');
        return book;
    }

    getBookById(id) {
        return this.books.find(b => b.id === id);
    }

    searchByTitle(title) {
        return this.books.filter(b => 
            b.title.toLowerCase().includes(title.toLowerCase())
        );
    }

    searchByYear(year) {
        return this.books.filter(b => 
            b.publicationYear.toString().includes(year)
        );
    }

    searchByAuthor(author) {
        return this.books.filter(b => 
            b.author.toLowerCase().includes(author.toLowerCase())
        );
    }

    combinedSearch(title, year, author) {
        return this.books.filter(b => {
            const titleMatch = !title || b.title.toLowerCase().includes(title.toLowerCase());
            const yearMatch = !year || b.publicationYear.toString().includes(year);
            const authorMatch = !author || b.author.toLowerCase().includes(author.toLowerCase());
            return titleMatch && yearMatch && authorMatch;
        });
    }

    editBook(bookId) {
        const book = this.getBookById(bookId);
        if (!book) {
            console.log('کتاب مورد نظر یافت نشد!');
            return false;
        }

        console.log('\n--- ویرایش کتاب ---');
        console.log('اطلاعات فعلی:');
        console.log(`عنوان: ${book.title}`);
        console.log(`نویسنده: ${book.author}`);
        console.log(`سال نشر: ${book.publicationYear}`);
        console.log(`شابک: ${book.isbn}`);
        console.log(`دسته‌بندی: ${book.category}`);

        console.log('\nاطلاعات جدید را وارد کنید (برای حفظ اطلاعات فعلی Enter بزنید):');
        
        const newTitle = readlineSync.question(`عنوان جدید (${book.title}): `);
        const newAuthor = readlineSync.question(`نویسنده جدید (${book.author}): `);
        const newYear = readlineSync.question(`سال نشر جدید (${book.publicationYear}): `);
        const newIsbn = readlineSync.question(`شابک جدید (${book.isbn}): `);
        const newCategory = readlineSync.question(`دسته‌بندی جدید (${book.category}): `);

        if (newTitle) book.title = newTitle;
        if (newAuthor) book.author = newAuthor;
        if (newYear) book.publicationYear = parseInt(newYear);
        if (newIsbn) book.isbn = newIsbn;
        if (newCategory) book.category = newCategory;

        console.log('کتاب با موفقیت ویرایش شد!');
        return true;
    }

    getAllBooks() {
        return this.books;
    }

    getAvailableBooks() {
        return this.books.filter(b => b.isAvailable);
    }

    getBorrowedBooks() {
        return this.books.filter(b => !b.isAvailable);
    }

    setBookAvailability(bookId, isAvailable) {
        const book = this.getBookById(bookId);
        if (book) {
            book.isAvailable = isAvailable;
            return true;
        }
        return false;
    }

    getBooksAddedByEmployee(employeeId) {
        return this.books.filter(b => b.addedBy === employeeId);
    }
}

module.exports = BookManager;
