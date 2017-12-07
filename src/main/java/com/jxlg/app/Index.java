package com.jxlg.app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

/**
 * @author zhouboxi
 * @create 2017-12-06 19:24
 **/
public class Index {
    //写索引实例

    private IndexWriter writer;

    /**
     * 获取文件目录 构造方法实例化IndexWriter
     * @param indexDir
     * @throws Exception
     */
    public Index(String indexDir)throws  Exception{
        Directory dir= FSDirectory.open(Paths.get(indexDir));
        //标准分词器
        Analyzer analyzer=new StandardAnalyzer();
        //传入分析器
        IndexWriterConfig conf=new IndexWriterConfig(analyzer);
        //传入目录和config
        writer=new IndexWriter(dir,conf);
    }

    /**
     * 关闭写索引
     * @throws Exception
     */
    public void close()throws Exception{
        writer.close();
    }

    /**
     * 索引指定目录的所有文件
     * @param dataDir
     * @throws Exception
     */
    public int index(String dataDir)throws Exception{
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            indexFile(file);
        }
        //得到索引文件个数
        return writer.numDocs();
    }

    /**
     * 索引指定的文件
     * @param file
     */
    private void indexFile(File file) throws Exception {
        System.out.println("索引文件"+file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    /**
     * 获取文档,在文档里再设置每个字段
     * @param file
     */
    private Document getDocument(File file) throws Exception{
        Document document=new Document();
        document.add(new TextField("contents",new FileReader(file)));
        document.add(new TextField("filename",file.getName(), Field.Store.YES));
        document.add(new TextField("fullPath",file.getCanonicalPath(), Field.Store.YES));
        return  document;
    }

    public static void main(String[] args) {
        //输出
        String indexDir="D:\\lucene";
        //输入
        String dataDir="D:\\lucene\\data";
        Index index = null;
        int num =0;
        long start=System.currentTimeMillis();
       try {
           index= new Index(indexDir);
           num = index.index(dataDir);
       }catch (Exception e){
           System.out.println("异常");
       }finally {
           try {
               index.close();
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       long end=System.currentTimeMillis();
       System.out.print("花费了"+(end-start)+"MS");
       System.out.println("索引"+num+"个文件");
    }

}

