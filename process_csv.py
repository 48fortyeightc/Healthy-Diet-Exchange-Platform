import csv
import collections

csv_path = r'D:\A\数据库\豆果美食-采集的数据.csv'
sql_path = 'database/import_data.sql'

recipes = collections.defaultdict(lambda: {
    'title': '',
    'cover_image': '',
    'author': '豆果达人',
    'steps': [],
    'ingredients': []
})

# 尝试不同的编码
encodings = ['utf-8-sig', 'utf-8', 'gb18030', 'ansi']

for enc in encodings:
    try:
        print(f"尝试编码: {enc}")
        with open(csv_path, mode='r', encoding=enc) as f:
            # 检查第一行标题
            header = f.readline()
            print(f"第一行预览: {header[:50]}")
            if '标题' in header:
                f.seek(0)
                reader = csv.DictReader(f)
                count = 0
                for row in reader:
                    title = row.get('标题', '').strip()
                    if not title: continue
                    
                    recipes[title]['title'] = title
                    img = row.get('标题链接1', '').strip() or row.get('缩略图', '').strip()
                    recipes[title]['cover_image'] = img
                    
                    author_raw = row.get('item1', '')
                    if 'by' in author_raw:
                        recipes[title]['author'] = author_raw.replace('by', '').replace('\n', '').strip()
                    
                    step_text = row.get('stepinfo', '').strip()
                    if step_text and step_text not in recipes[title]['steps']:
                        recipes[title]['steps'].append(step_text)
                    
                    for i in range(2, 10):
                        ing = row.get(f'字段{i}', '').strip()
                        if ing and ing not in recipes[title]['ingredients']:
                            ing = " ".join(ing.split())
                            recipes[title]['ingredients'].append(ing)
                    count += 1
                
                if count > 0:
                    print(f"使用 {enc} 成功读取 {count} 行数据")
                    break
    except Exception as e:
        print(f"{enc} 失败: {e}")

if not recipes:
    print("未能读取到任何食谱，请重新检查 CSV。")
else:
    with open(sql_path, 'w', encoding='utf-8') as f:
        f.write("USE healthy_diet_db;\n\n")
        f.write("SET FOREIGN_KEY_CHECKS = 0;\n")
        user_id_map = {}
        for idx, (title, data) in enumerate(recipes.items(), start=10):
            author = data['author']
            if author not in user_id_map:
                user_id_map[author] = idx + 1000
                f.write(f"INSERT IGNORE INTO users (id, username, password_hash, role) VALUES ({user_id_map[author]}, '{author}', 'pwd_hash', 'USER');\n")
                f.write(f"INSERT IGNORE INTO user_profiles (user_id, nickname) VALUES ({user_id_map[author]}, '{author}');\n")
            recipe_id = idx
            clean_title = title.replace("'", "''")
            f.write(f"INSERT INTO recipes (id, author_id, title, cover_image, status) VALUES ({recipe_id}, {user_id_map[author]}, '{clean_title}', '{data['cover_image']}', 'PUBLISHED');\n")
            for s_idx, step in enumerate(data['steps'], start=1):
                clean_step = step.replace("'", "''").replace("\n", " ")
                f.write(f"INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES ({recipe_id}, {s_idx}, '{clean_step}');\n")
            for ing in data['ingredients']:
                clean_ing = ing.replace("'", "''")
                parts = clean_ing.split()
                if parts:
                    name = parts[0]
                    amount = " ".join(parts[1:]) if len(parts) > 1 else "适量"
                    f.write(f"INSERT INTO recipe_ingredients (recipe_id, name, amount) VALUES ({recipe_id}, '{name}', '{amount}');\n")
            f.write("\n")
        f.write("SET FOREIGN_KEY_CHECKS = 1;\n")
        print(f"成功生成 SQL 导入文件: {sql_path}, 共处理 {len(recipes)} 个食谱")
