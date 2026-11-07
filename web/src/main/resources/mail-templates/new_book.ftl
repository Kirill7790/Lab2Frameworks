<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Нова книга в каталозі</title>
</head>
<body style="margin:0; padding:0; background-color:#f4f4f4; font-family:Arial, sans-serif;">

<table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f4; padding:20px 0;">
    <tr>
        <td align="center">
            <table width="600" cellpadding="0" cellspacing="0"
                   style="background-color:#ffffff; border-radius:8px; overflow:hidden;
                          box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <!-- ===== Логотип ===== -->
                <tr>
                    <td style="padding:20px; text-align:center; background-color:#fafafa;
                               border-bottom:1px solid #eee;">
                        <img src="https://raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png"
                             alt="BookApp"
                             style="width:150px; margin-bottom:10px;"/>
                        <h1 style="margin:0; font-size:22px; color:#333;">Book Catalog</h1>
                    </td>
                </tr>

                <!-- ===== Панель повідомлення ===== -->
                <tr>
                    <td style="padding:30px 30px 10px 30px;">
                        <div style="padding:15px; background-color:#e8f5e9;
                                    border-left:4px solid #4caf50; border-radius:4px;">
                            <p style="margin:0; font-size:16px; color:#2e7d32;">
                                <b>У каталог додано нову книгу!</b>
                            </p>
                        </div>
                    </td>
                </tr>

                <!-- ===== Дані книги ===== -->
                <tr>
                    <td style="padding:20px 30px;">
                        <table width="100%" cellpadding="8" cellspacing="0"
                               style="border-collapse:collapse; font-size:15px;">
                            <tr style="background-color:#fafafa;">
                                <td style="width:120px; color:#666;"><b>Назва:</b></td>
                                <td style="color:#222;">${title}</td>
                            </tr>
                            <tr>
                                <td style="color:#666;"><b>Автор:</b></td>
                                <td style="color:#222;">${author}</td>
                            </tr>
                            <tr style="background-color:#fafafa;">
                                <td style="color:#666;"><b>Рік:</b></td>
                                <td style="color:#222;">${year}</td>
                            </tr>
                            <tr>
                                <td style="color:#666;"><b>Додано:</b></td>
                                <td style="color:#222;">${added}</td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <!-- ===== Умовний блок: раритет ===== -->
                <#if year < 2000>
                <tr>
                    <td style="padding:0 30px 20px 30px;">
                        <div style="padding:12px; background-color:#fff8e1;
                                    border:1px solid #ffb300; border-radius:4px;">
                            <p style="margin:0; color:#a36b00; font-size:14px;">
                                <b>Раритетне видання!</b> Ця книга старша за 2000 рік —
                                справжня цінність для колекції.
                            </p>
                        </div>
                    </td>
                </tr>
                </#if>

                <!-- ===== Footer ===== -->
                <tr>
                    <td style="padding:20px 30px; background-color:#fafafa;
                               border-top:1px solid #eee; text-align:center;">
                        <p style="margin:0; font-size:13px; color:#888;">
                            BookApp © 2026<br/>
                            <a href="https://github.com/VladPiatachenko/MPF_Labs"
                               style="color:#1976d2; text-decoration:none;">
                                github.com
                            </a>
                        </p>
                    </td>
                </tr>

            </table>
        </td>
    </tr>
</table>

</body>
</html>