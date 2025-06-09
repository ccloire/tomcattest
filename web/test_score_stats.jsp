<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>成绩统计测试页面</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    .container { max-width: 1000px; margin: 0 auto; }
    .panel { border: 1px solid #ddd; padding: 15px; margin-bottom: 20px; }
    pre { background-color: #f5f5f5; padding: 10px; overflow: auto; }
  </style>
</head>
<body>
<div class="container">
  <h1>成绩统计测试页面</h1>

  <div class="panel">
    <h3>基本信息</h3>
    <p>当前URL: <script>document.write(window.location.href)</script></p>
    <p>用户代理: <script>document.write(navigator.userAgent)</script></p>
  </div>

  <div class="panel">
    <h3>API测试</h3>
    <form id="apiForm">
      <div>
        <label for="courseId">课程ID:</label>
        <input type="text" id="courseId" value="C001">
      </div>
      <div style="margin-top: 10px;">
        <button type="button" id="testBtn">测试API</button>
      </div>
    </form>
  </div>

  <div class="panel">
    <h3>响应结果</h3>
    <div id="loadingIndicator" style="display: none;">正在加载数据...</div>
    <pre id="resultContainer">点击"测试API"按钮获取数据</pre>
  </div>
</div>

<script>
  // 确保页面加载完成
  document.addEventListener('DOMContentLoaded', function() {
    console.log('页面已加载完成');

    // 获取元素
    var testBtn = document.getElementById('testBtn');
    var courseIdInput = document.getElementById('courseId');
    var resultContainer = document.getElementById('resultContainer');
    var loadingIndicator = document.getElementById('loadingIndicator');

    // 添加按钮点击事件
    testBtn.addEventListener('click', function() {
      var courseId = courseIdInput.value;
      if (!courseId) {
        alert('请输入课程ID');
        return;
      }

      // 显示加载指示器
      loadingIndicator.style.display = 'block';
      resultContainer.textContent = '正在请求数据...';

      // 构建API URL
      var baseUrl = window.location.origin + window.location.pathname.substring(0, window.location.pathname.indexOf('/test_score_stats.jsp'));
      var apiUrl = baseUrl + '/teacher/score/getStatistics?courseId=' + encodeURIComponent(courseId);

      console.log('请求URL:', apiUrl);

      // 使用fetch API发送请求
      fetch(apiUrl)
              .then(function(response) {
                console.log('响应状态:', response.status);
                return response.text();
              })
              .then(function(text) {
                console.log('响应文本:', text);
                loadingIndicator.style.display = 'none';

                try {
                  // 尝试解析JSON
                  var data = JSON.parse(text);
                  resultContainer.textContent = JSON.stringify(data, null, 2);
                } catch (e) {
                  // 如果不是JSON，直接显示文本
                  resultContainer.textContent = text;
                }
              })
              .catch(function(error) {
                console.error('请求错误:', error);
                loadingIndicator.style.display = 'none';
                resultContainer.textContent = '请求出错: ' + error.message;
              });
    });
  });
</script>
</body>
</html>