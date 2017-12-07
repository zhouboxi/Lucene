package com.jxlg.app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;


/**
 * @author zhouboxi
 * @create 2017-12-07 9:28
 **/
public class CRUDIndex {
    private String ids[]={"1","2","3"};
    private String city[]={"上海","北京","深圳"};
    private String desc[]={
            "上海是一个繁华的城市",
            "北京是中国人眼中的帝都",
            "深圳是一个年轻的城市"};


    private Directory dir;
    //StringField不会进行分词
    @Before
    public void setUp() throws Exception{
        //FSDirectory是Lucene对文件系统的操作
        dir=FSDirectory.open(Paths.get("D:\\lucene\\lucene2"));
//        IndexWriter writer = getIndexWriter();
        //对文档的添加
//        for (int i = 0; i < ids.length; i++) {
//            //创建文档对象
//            Document document = new Document();
//            //对文档进行1属性添加 Field.Store.YES是属性保存在本地
//            document.add(new StringField("id",ids[i], Field.Store.YES));
//            document.add(new StringField("city",city[i], Field.Store.YES));
//            document.add(new TextField("desc",desc[i], Field.Store.NO));
////            //把文档写入
//            writer.addDocument(document);
//        }
//        writer.close();
    }

    /**
     * 获取IndexWriter对象
     * @return
     * @throws Exception
     */
    private IndexWriter getIndexWriter()throws Exception{
        // 1、创建一个分析器对象 使用哪种分析器
        Analyzer analyzer = new SmartChineseAnalyzer();
        //IndexWriterConfig对象创建,并获取IndexWriter对象
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //获取IndexWriter对象 把要分析的文件地址和分析器传入
        IndexWriter writer = new IndexWriter(dir, config);
        return  writer;
    }

    /**
     * 测试写入文档个数
     * @throws Exception
     */
    @Test
    public void testIndexWriter()throws Exception{
        IndexWriter writer = getIndexWriter();
        System.out.println("写入了"+writer.numDocs()+"个文档");
        writer.close();
    }

    /**
     * 对文档的读取
     * @throws Exception
     */
    @Test
    public void testIndexReader()throws Exception{
        DirectoryReader reader = DirectoryReader.open(dir);
        System.out.println("最大文档数"+reader.maxDoc());
        System.out.println("实际文档数"+reader.numDocs());
    }

    /**
     * 删除测试 在合并前 只是不起作用没有删除
     * @throws Exception
     */
    @Test
    public void testDeleteBeforeMerge()throws Exception{
        IndexWriter writer = getIndexWriter();
        System.out.println("删除前"+writer.numDocs());
        writer.deleteDocuments(new Term("id","1"));
        writer.commit();
        System.out.println("删除后numDocs"+writer.numDocs());
        System.out.println("删除后maxDoc"+writer.maxDoc());
        writer.close();
    }

    /**
     * 测试删除,在合并后
     * @throws Exception
     */
    @Test
    public void testDeleteAfter() throws Exception {
        IndexWriter writer = getIndexWriter();
        writer.deleteDocuments(new Term("id","1"));
        //强制删除
        writer.forceMergeDeletes();
        writer.commit();
        System.out.println("删除后numDocs"+writer.numDocs());
        System.out.println("删除后maxDoc"+writer.maxDoc());
        writer.close();
    }

    /**
     * 更新
     * @throws Exception
     */
    @Test
    public void testUpdate()throws Exception{
        IndexWriter writer = getIndexWriter();
        Document document = new Document();
        document.add(new StringField("id","2", Field.Store.YES));
        document.add(new StringField("city","南昌", Field.Store.YES));
        document.add(new TextField("desc","南昌是一个有文化的城市", Field.Store.NO));
        writer.updateDocument(new Term("id","2"),document);
        writer.close();
    }

    /**
     * 使用QueryParser方式查询
     * @throws Exception
     */
    @Test
    public void search() throws Exception{
        //读取生成的文档
        DirectoryReader reader = DirectoryReader.open(dir);
        //对文档进行搜索
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //采用中文搜索
        Analyzer analyzer = new SmartChineseAnalyzer();
        //使用QueryParser方式搜索得到搜索内容 搜索内容id,搜索方式
        QueryParser desc = new QueryParser("desc",analyzer);
        String key="城市";
        //对指定词进行转换
        Query query = desc.parse(key);
        //得到查询的结果,只查询10个
        TopDocs search = indexSearcher.search(query, 10);
        //得到排名文档
        for (ScoreDoc scoreDoc : search.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            //输出文档查询到的指定内容
            System.out.println(doc.get("city"));
        }
        System.out.println("查询到"+search.totalHits+"条记录");
        reader.close();
    }

    @Test
    public void search2() throws Exception{
        //文档的读取
        DirectoryReader reader = DirectoryReader.open(dir);
        //声明查询
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //采用中文搜索
        Analyzer analyzer = new SmartChineseAnalyzer();
        String desc="desc";
        String key="城市";
        //创建Term对象传入搜索的对象和搜索关键字
        Term term = new Term(desc, key);
        //使用termQuery
        t

    }

}

