import java.util.List;

/**
 * 既有测试（回归网，请勿删改）。
 *
 * 约定：测试类名以 Test 结尾；测试方法为无参 public void 且以 test 开头；
 * 使用 Assert 帮助类断言，失败抛 AssertionError，由 TestRunner 统一统计。
 *
 * 注意：本文件不覆盖 search 与删除图书（这两处是考核任务一/任务二的考场），
 * 也不得为了迁就实现而修改种子数据。
 */
public class LibraryTest {

    /** 种子数据：3 本预置图书，第二本为已借出。 */
    public void testSeedDataLoaded() {
        Library library = new Library(true);
        List<Book> books = library.listAll();
        Assert.assertEquals(3, books.size(), "种子应有 3 本图书");

        Book first = books.get(0);
        Assert.assertEquals(1, first.getId(), "第一本编号应为 1");
        Assert.assertEquals("Java入门", first.getName(), "第一本书名");
        Assert.assertEquals("张三", first.getAuthor(), "第一本作者");
        Assert.assertEquals(59.5, first.getPrice(), "第一本价格");
        Assert.assertFalse(first.isBorrowed(), "第一本应为未借出");

        Book second = books.get(1);
        Assert.assertEquals(2, second.getId(), "第二本编号应为 2");
        Assert.assertEquals("数据结构", second.getName(), "第二本书名");
        Assert.assertEquals(45.0, second.getPrice(), "第二本价格");
        Assert.assertTrue(second.isBorrowed(), "第二本应预置为已借出");

        Book third = books.get(2);
        Assert.assertEquals(3, third.getId(), "第三本编号应为 3");
        Assert.assertEquals("算法导论", third.getName(), "第三本书名");
        Assert.assertFalse(third.isBorrowed(), "第三本应为未借出");
    }

    /** 空构造参数：不预置数据。 */
    public void testEmptyLibrary() {
        Library library = new Library(false);
        Assert.assertEquals(0, library.listAll().size(), "空库应没有任何图书");
        Assert.assertNull(library.findById(1), "空库按编号查找应为 null");
    }

    /** 添加图书：种子之后的编号从 4 开始自增（删除不复用编号，见任务二）。 */
    public void testAddAssignsSequentialIdsAfterSeed() {
        Library library = new Library(true);
        Book fourth = library.add("操作系统", "赵六", 70.0);
        Assert.assertEquals(4, fourth.getId(), "种子后首次添加应得编号 4");
        Book fifth = library.add("计算机网络", "赵六", 68.0);
        Assert.assertEquals(5, fifth.getId(), "第二次添加应得编号 5");
        Assert.assertFalse(fifth.isBorrowed(), "新书应为未借出");
        Assert.assertEquals("操作系统", fourth.getName(), "书名应正确保存");
        Assert.assertEquals(70.0, fourth.getPrice(), "价格应正确保存");
    }

    /** 借阅/归还：四种状态提示语。 */
    public void testBorrowAndReturnMessages() {
        Library library = new Library(true);
        Assert.assertEquals("借阅成功", library.borrow(1), "未借出借阅应成功");
        Assert.assertTrue(library.findById(1).isBorrowed(), "借阅后状态应为已借出");
        Assert.assertEquals("该图书已被借出", library.borrow(1), "重复借阅应提示已被借出");
        Assert.assertEquals("归还成功", library.returnBook(1), "已借出归还应成功");
        Assert.assertFalse(library.findById(1).isBorrowed(), "归还后状态应为未借出");
        Assert.assertEquals("该图书未被借出", library.returnBook(1), "重复归还应提示未被借出");
    }

    /** 借阅预置为已借出的书：先提示已被借出，归还后恢复。 */
    public void testBorrowSeededBorrowedBook() {
        Library library = new Library(true);
        Assert.assertEquals("该图书已被借出", library.borrow(2), "预置已借出的书不能再借");
        Assert.assertEquals("归还成功", library.returnBook(2), "预置已借出的书可归还");
        Assert.assertFalse(library.findById(2).isBorrowed(), "归还后应为未借出");
    }

    /** 编号不存在：借阅与归还均提示图书编号不存在。 */
    public void testUnknownIdMessages() {
        Library library = new Library(true);
        Assert.assertEquals("图书编号不存在", library.borrow(999), "不存在编号借阅");
        Assert.assertEquals("图书编号不存在", library.returnBook(999), "不存在编号归还");
        Assert.assertNull(library.findById(0), "编号 0 不存在");
        Assert.assertNull(library.findById(-1), "负编号不存在");
    }

    /** 列表顺序：按添加顺序返回，且返回副本不影响内部数据。 */
    public void testListAllOrderAndCopy() {
        Library library = new Library(true);
        List<Book> books = library.listAll();
        Assert.assertEquals(3, books.size(), "列表应有 3 本");
        Assert.assertEquals(1, books.get(0).getId(), "顺序按编号");
        Assert.assertEquals(2, books.get(1).getId(), "顺序按编号");
        Assert.assertEquals(3, books.get(2).getId(), "顺序按编号");
        books.clear();
        Assert.assertEquals(3, library.listAll().size(), "返回副本，清空不影响内部数据");
    }
}
