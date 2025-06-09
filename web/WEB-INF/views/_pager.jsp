<%@ page import="com.hello.utils.vo.PagerVO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 获取参数
    String pageUrl = request.getParameter("pageUrl");
    PagerVO pager = (PagerVO) request.getAttribute("pager");

    if (pager == null || pageUrl == null) {
        return;
    }

    int currentPage = pager.getCurrent();
    int totalPages = pager.getTotalPages();
    int totalRecords = pager.getTotal();
%>

<div class="row">
    <div class="col-xs-12 col-md-7">
        <nav>
            <ul class="pagination">
                <li class="<%= currentPage == 1 ? "disabled" : "" %>">
                    <a href="<%= currentPage == 1 ? "javascript:void(0)" : pageUrl + "?page=" + (currentPage - 1) %>">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <%
                    // 显示最多5页的导航
                    int startPage = Math.max(1, currentPage - 2);
                    int endPage = Math.min(totalPages, startPage + 4);
                    startPage = Math.max(1, endPage - 4);

                    for (int i = startPage; i <= endPage; i++) {
                %>
                <li class="<%= i == currentPage ? "active" : "" %>">
                    <a href="<%= i == currentPage ? "javascript:void(0)" : pageUrl + "?page=" + i %>"><%= i %></a>
                </li>
                <% } %>

                <li class="<%= currentPage == totalPages ? "disabled" : "" %>">
                    <a href="<%= currentPage == totalPages ? "javascript:void(0)" : pageUrl + "?page=" + (currentPage + 1) %>">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
    <div class="col-xs-12 col-md-5 text-right">
        <span class="pagination-info">显示 <%= pager.getList() != null ? pager.getList().size() : 0 %> 条，共 <%= totalRecords %> 条记录</span>
    </div>
</div>
