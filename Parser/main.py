import os

import requests
import json
from bs4 import BeautifulSoup

url = 'https://zagadki-dlya-detej.ru/zagadki-korotkie/'
filePath = 'questions.json'

response = requests.get(url)
soup = BeautifulSoup(response.text, 'html.parser')

questions = []

quest_items = soup.findAll('div', class_="quest_item")

for quest_item in quest_items:
    question_lines = quest_item.find('div', class_='quest_question').findAll('p')
    answer = quest_item.find('div', class_='quest_answer')['data-answer']
    question = ''
    for question_line in question_lines:
        question += f'\n{question_line.text}'

    questions.append({'question': question, 'answer': answer})

if os.path.exists(filePath):
    os.remove(filePath)

with open(filePath, 'x') as fp:
    json.dump(questions, fp)
