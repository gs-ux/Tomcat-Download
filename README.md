# Tomcat-Download
有一种常见的需求，当我们点击一个连接的时候，他会弹出来一个下载窗口，如下：

![](https://www.gaosong.site/2020/03/30/Tomcat%E7%94%A8%E6%88%B7%E6%96%87%E4%BB%B6%E4%B8%8B%E8%BD%BD%E6%8E%A5%E5%8F%A3/20200330pic1.png)

接下来我们就用Java语言，Tomcat服务器来实现这种需求。

首先，我们需要把图片放到web下的一个img文件夹下面，这就是图片资源路径。

![](https://www.gaosong.site/2020/03/30/Tomcat%E7%94%A8%E6%88%B7%E6%96%87%E4%BB%B6%E4%B8%8B%E8%BD%BD%E6%8E%A5%E5%8F%A3/20200330pic2.png)

接下来我们创建一个**WebServlet**，名字就叫**DownloadServlet**，让它继承**HttpServlet**，实现里面的doPost方法即可，doGet方法直接也写成调用doPost方法。doPost方法有以下步骤：

1. 通过request获取参数文件路径

   ```java
   String fileName = request.getParameter("fileName");//获取request里面的fileName参数值
   ```

2. 获取服务器文件流

   ```java
   ServletContext servletContext = this.getServletContext();//获取getServletContext对象
   String realPath = servletContext.getRealPath("/img/" + fileName);//根据fileName，调用servletContext的getRealPath方法，获取服务器的下载文件路径
   FileInputStream fis = new FileInputStream(realPath);//设置字节流，读取文件
   ```

3. 设置响应头

   ```java
   response.setHeader("Content-Type",servletContext.getMimeType(fileName));//获取并设置文件类型，如jpg形式，text/html形式等等
   response.setHeader("Content-Disposition", "attachment;filename="+fileName);//这个就是告知我们是要下载文件，而不是预览文件，fileName为下载时提示保存文件名
   ```

4. 写到输出流中

   ```java
   ServletOutputStream os = response.getOutputStream();
   byte[] bytes = new byte[1024];
   int len = 0;
   while ((len = (fis.read(bytes))) != -1) {
       os.write(bytes, 0 , len);
   }
   fis.close();
   ```

以上就完成了，我们在a标签里href设置为`/DownloadServlet?fileName=qq.ico`时，运行起来Tomcat，打开浏览器，点击a标签，就会弹出来下载的提示了。

![](https://www.gaosong.site/2020/03/30/Tomcat%E7%94%A8%E6%88%B7%E6%96%87%E4%BB%B6%E4%B8%8B%E8%BD%BD%E6%8E%A5%E5%8F%A3/20200330pic3.png)

但是这样还有一个问题就是如果名字有中文的话会出现乱码情况，我们还需设置这一问题。

首先我们新建一个utils包，创建一个名字为`DownLoadUtils`的java文件，文件内容如下：

```java
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;


public class DownLoadUtils {

    public static String getFileName(String agent, String fileName) throws UnsupportedEncodingException {
        if (agent.contains("MSIE")) {
            // IE浏览器
            fileName = URLEncoder.encode(fileName, "utf-8");
            fileName = fileName.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] textByte = fileName.getBytes("UTF-8");
            fileName = "=?utf-8?B?" + encoder.encodeToString(textByte) + "?=";

        } else {
            // 其它浏览器
            fileName = URLEncoder.encode(fileName, "utf-8");
        }
        return fileName;
    }
}
```

再回到我们的**DownloadServlet**文件中，在设置响应头设置`Content-Type`后、设置`Content-Disposition`前，添加下面两行代码即可：

```java
//解决下载是名称中文乱码问题
String agent = request.getHeader("user-agent");
fileName = DownLoadUtils.getFileName(agent, fileName);
```

这样设置完成之后就大功告成，这样如果名字带有中文的话，它也能正常显示出来了。

最后再附上`github`[地址](https://github.com/gs-ux/Tomcat-Download)。