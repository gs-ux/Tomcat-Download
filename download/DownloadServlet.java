package cn.gs.download;

import cn.gs.utils.DownLoadUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //通过request获取参数文件路径
        String fileName = request.getParameter("fileName");//获取request里面的fileName参数值
        //获取服务器文件流
        ServletContext servletContext = this.getServletContext();//获取getServletContext对象
        String realPath = servletContext.getRealPath("/img/" + fileName);//根据fileName，调用servletContext的getRealPath方法，获取服务器的下载文件路径
        FileInputStream fis = new FileInputStream(realPath);//设置字节流，读取文件

        //设置响应头
        //获取并设置后缀名，jpg形式，text/html形式等等
        response.setHeader("Content-Type",servletContext.getMimeType(fileName));

        //解决下载是名称中文乱码问题
        String agent = request.getHeader("user-agent");
        fileName = DownLoadUtils.getFileName(agent, fileName);

        response.setHeader("Content-Disposition", "attachment;filename="+fileName);//这个就是告知我们是要下载文件，而不是预览文件，fileName为下载时提示保存文件名

        //写到输出流中
        ServletOutputStream os = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = (fis.read(bytes))) != -1) {
            os.write(bytes, 0 , len);
        }
        fis.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
