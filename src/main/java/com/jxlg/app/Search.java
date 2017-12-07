package com.jxlg.app;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author zhouboxi
 * @create 2017-12-06 20:07
 **/
public class Search {
    public  static  void  search(String dataDir,String value) throws IOException, ParseException {
        FSDirectory dir = FSDirectory.open(Paths.get(dataDir));
        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher indexSearcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser contents = new QueryParser("contents", analyzer);
        Query query = contents.parse(value);
        TopDocs search = indexSearcher.search(query, 10);
        for (ScoreDoc scoreDoc : search.scoreDocs) {
            Document doc=indexSearcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullPath"));
        }
        System.out.println("查询到"+search.totalHits+"条记录");
        reader.close();

    }

    public static void main(String[] args) {
        String dataDir="D:\\lucene";
        String value="custom directory";

        try {
            search(dataDir,value);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

