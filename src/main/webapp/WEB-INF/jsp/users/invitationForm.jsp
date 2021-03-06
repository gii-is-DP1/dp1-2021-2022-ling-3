<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="parchisoca" tagdir="/WEB-INF/tags" %>
<parchisoca:layout pageName="invitationForm">
    <div class="row">


        <div class="col-md-6 p-3 m-3 border border-secondary w-100 rounded">
            <h2 class="lead">Invite users</h2>
            <hr>
            <table class="table table-hover table-striped table-condensed">

                <thead>
                    <td>Username</td>
                    <td>First name</td>
                    <td>Last name</td>
                    <td>Email</td>
                    <td></td>
                </thead>
                <tbody>
                    <c:forEach items="${users}" var="user">
                            <td>
                                <c:out value="${user.username}" />
                            </td>
                            <td>
                                <c:out value="${user.firstname}" />
                            </td>
                            <td>
                                <c:out value="${user.lastname}" />
                            </td>
                            <td>
                                <c:out value="${user.email}" />
                            </td>
                            <td>
                                <a href="/invite/${user.username}"> Invite User</a>
                            </td>
                            </tr>
                    </c:forEach>
                </tbody>

            </table>
            <div>
             <c:if test="${empty users}">
                            <div>
                                No users to invite.
                            </div>
                        </c:if>

                    </div>



        </div>

</parchisoca:layout>
