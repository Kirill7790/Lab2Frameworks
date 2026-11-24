<!DOCTYPE html>
<html>
<body style="font-family:Arial,sans-serif;background:#f4f4f4;padding:20px;">
  <div style="max-width:520px;margin:0 auto;background:#fff;border-radius:8px;padding:30px;">
    <h2>Вітаємо, ${username}!</h2>
    <p>Дякуємо за реєстрацію в BookApp. Підтвердіть акаунт, перейшовши за посиланням:</p>
    <p><a href="${confirmUrl}" style="color:#1976d2;">${confirmUrl}</a></p>
    <p style="color:#888;font-size:13px;">Або введіть код вручну: <b>${code}</b></p>
  </div>
</body>
</html>