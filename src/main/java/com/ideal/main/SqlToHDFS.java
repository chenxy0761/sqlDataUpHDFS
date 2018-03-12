package com.ideal.main;

import com.ideal.util.SqlUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SqlToHDFS {
    private static Logger logger = Logger.getLogger(SqlToHDFS.class);

    public  boolean SqlToHdfs(InputStream in,String tablename) {
        SqlUtil s = new SqlUtil();
        Configuration conf = new Configuration();
        FSDataOutputStream outputStream = null;
        boolean result = true;
        FileSystem hs = null;
        try {

                hs = FileSystem.get(URI.create(s.getHdfs()), conf);
                if (!hs.exists(new Path(s.getHdfs() + s.getHdfsPath() + "/" + tablename + "/"))) {
                    hs.mkdirs(new Path(s.getHdfs() + s.getHdfsPath()));
                }
                outputStream = hs.create(new Path(s.getHdfs() + s.getHdfsPath() + "/" + tablename + "/" + tablename + ".txt"));
                IOUtils.copyBytes(in, outputStream, conf, false);

                if (in != null) {
                    in.close();
                    logger.info("流关闭成功！");
                }

        } catch (IOException e) {
            logger.error("数据上传失败" + e.getMessage());
        }
     return result;
    }
    public static void main(String[] args) {
        SqlUtil s=new SqlUtil();
        String[] tablesname=s.getTablename().split(";");
        String date = s.getDATE();
        if (date.equals("")){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date d=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            d = calendar.getTime();
            date = sdf.format(d);
        }
        for(String tablename:tablesname) {
        SqlToHDFS fp=new SqlToHDFS();
            if (fp.SqlToHdfs(SqlServerJDBC.Exp(tablename,date),tablename)) {
                logger.info("OK");
            } else {
                logger.info("FAIL");
            }
        }

    }
}





