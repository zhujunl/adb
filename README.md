## 人证核验-ADB版

最后一次更新时间：2021年2月1日

### 祖传工程警告

此版本的身份证感应区并不实时监听，这意味核验人员无法通过放置身份证来主动发起一次人证核验流程。此版本的使用需要结合着PC端程序共同使用，PC端C++版本及OCX版本由李鑫进行开发，WebSocket版本由刘凡进行开发，其核心方案为：将电脑和MR860通过双头USB线进行连接，MR860端人证核验应用开启一个SocketServer监听2235端口上，PC端通过adb forward命令将指定端口与2235端口进行映射，此时，PC端可以通过Socket连接指定端口达到连接人证核验应用开启的SocketServer上，连接成功后，PC端和MR860端就可以通过Socket进行数据传输了。

Socket连接成功后，由PC端根据协议下发关键命令字，MR860端收到命令字后，根据命令字进行特定操作，如开始监听身份证阅读器，让核验人员可以进行核验，或者是让核验人员按压一次手指，获取核验人员的指纹，或者是进行一次拍照，命令会在15秒内处理完成，命令完成后或者15秒超时后，MR860端将按照协议返回数据，由PC端进行解析。

具体协议内容请参考人证核验说明目录下的ADB开发资料文件夹。