import random

print("🎮 أهلاً بك في لعبتي!")
print("خمن الرقم من 1 إلى 10")

number = random.randint(1, 10)

guess = int(input("اكتب تخمينك: "))

if guess == number:
    print("🔥 برافو! خمنت الرقم صح!")
else:
    print("❌ غلط!")
    print("الرقم الصحيح هو:", number)

print("شكراً للعب! ❤️")
