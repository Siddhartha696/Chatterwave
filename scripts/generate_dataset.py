import json
import os
import sys
import shutil
import re


# =========================================
# CHATTERWAVE DATASET GENERATOR
# =========================================


if len(sys.argv) != 4:
    print(
        "Usage: python3 scripts/generate_dataset.py "
        "<dataset.log> <analysis_id> <display_name>"
    )
    sys.exit(1)


input_file = sys.argv[1]
analysis_id = sys.argv[2]
display_name = sys.argv[3]

project_dir = os.path.expanduser("~/ChatterWave")

analysis_dir = os.path.join(
    project_dir,
    "analysis_runs",
    analysis_id
)

dashboard_dir = os.path.join(
    project_dir,
    "dashboard"
)

datasets_dir = os.path.join(
    dashboard_dir,
    "datasets"
)

dataset_dir = os.path.join(
    datasets_dir,
    analysis_id
)


# =========================================
# CHECK FILES
# =========================================

if not os.path.isfile(input_file):
    print("ERROR: Dataset file not found:")
    print(input_file)
    sys.exit(1)


if not os.path.isdir(analysis_dir):
    print("ERROR: Analysis directory not found:")
    print(analysis_dir)
    sys.exit(1)


# =========================================
# DATASET PARSER
# Supports:
# 1. Original pipe-separated format
# 2. Multi-line key-value format
# =========================================

users = set()
hashtags = set()

total_posts = 0
total_engagement = 0

post = []


def process_post(lines):

    global total_posts
    global total_engagement

    if not lines:
        return

    # -------------------------------------
    # Detect pipe-separated format
    # -------------------------------------

    if "|" in lines[0]:

        fields = lines[0].split("|")

        if len(fields) < 6:
            return

        username = fields[1]
        hashtag_text = fields[3]

        likes = int(
            fields[4].split("=")[1]
        )

        shares = int(
            fields[5].split("=")[1]
        )

        users.add(username)

        for tag in hashtag_text.split(","):

            tag = tag.strip().lower()

            if tag:
                hashtags.add(tag)

        total_engagement += likes + shares
        total_posts += 1

        return

    # -------------------------------------
    # Multi-line key-value format
    # -------------------------------------

    username = None
    likes = 0
    shares = 0
    text = ""

    for line in lines:

        line = line.strip()

        if line.startswith("User:"):
            username = line.split(
                ":", 1
            )[1].strip()

        elif line.startswith("Likes:"):
            likes = int(
                line.split(
                    ":", 1
                )[1].strip()
            )

        elif line.startswith("Shares:"):
            shares = int(
                line.split(
                    ":", 1
                )[1].strip()
            )

        elif line.startswith("Text:"):
            text = line.split(
                ":", 1
            )[1].strip()

    if username is None:
        return

    users.add(username)

    # Extract hashtags from Text
    found_hashtags = re.findall(
        r"#[A-Za-z0-9_]+",
        text
    )

    for tag in found_hashtags:

        hashtags.add(
            tag.lower()
        )

    total_engagement += likes + shares
    total_posts += 1


# =========================================
# READ DATASET
# =========================================

with open(
    input_file,
    "r",
    encoding="utf-8"
) as infile:

    for line in infile:

        line = line.strip()

        if line == "###":

            process_post(post)

            post = []

        elif line:

            post.append(line)


# Handle final post
if post:
    process_post(post)


# =========================================
# READ MAPREDUCE RESULTS
# =========================================

def read_result(problem):

    path = os.path.join(
        analysis_dir,
        problem,
        "part-r-00000"
    )

    if not os.path.isfile(path):

        print(
            "ERROR: Missing result:"
        )

        print(path)

        sys.exit(1)

    with open(
        path,
        "r",
        encoding="utf-8"
    ) as f:

        return [
            line.strip()
            for line in f
            if line.strip()
        ]


# =========================================
# PROBLEM 1
# =========================================

problem1 = []

for line in read_result("problem1"):

    parts = line.split("\t")

    problem1.append({
        "hashtag": parts[0],
        "count": int(parts[1])
    })


# =========================================
# PROBLEM 2
# =========================================

problem2 = []

for line in read_result("problem2"):

    parts = line.split("\t")

    problem2.append({
        "hashtag": parts[0],
        "average": float(parts[1])
    })


# =========================================
# PROBLEM 3
# =========================================

problem3_lines = read_result("problem3")

if problem3_lines:

    parts = problem3_lines[0].split("\t")

    problem3 = {
        "user": parts[0],
        "engagement": int(parts[1])
    }

else:

    problem3 = {
        "user": "N/A",
        "engagement": 0
    }


# =========================================
# PROBLEM 4
# =========================================

problem4 = []

for line in read_result("problem4"):

    parts = line.split("\t")

    problem4.append({
        "hour": parts[0],
        "count": int(parts[1])
    })


# =========================================
# PROBLEM 5
# =========================================

problem5 = []

for line in read_result("problem5"):

    parts = line.split("\t")

    hashtags_pair = parts[0].split(",")

    problem5.append({
        "hashtag1": hashtags_pair[0],
        "hashtag2": hashtags_pair[1],
        "count": int(parts[1])
    })


# =========================================
# DASHBOARD DATA
# =========================================

dashboard_data = {

    "overview": {

        "total_posts":
            total_posts,

        "total_users":
            len(users),

        "total_hashtags":
            len(hashtags),

        "total_engagement":
            total_engagement,

        "analysis_problems":
            5
    },

    "problem1":
        problem1,

    "problem2":
        problem2,

    "problem3":
        problem3,

    "problem4":
        problem4,

    "problem5":
        problem5
}


# =========================================
# METADATA
# =========================================

metadata = {

    "id":
        analysis_id,

    "name":
        display_name,

    "description":
        "Automated MapReduce analysis of the supplied ChatterWave dataset.",

    "posts":
        total_posts,

    "users":
        len(users),

    "hashtags":
        len(hashtags),

    "total_engagement":
        total_engagement,

    "analyses": [

        "Problem 1 - Hashtag Frequency",

        "Problem 2 - Average Engagement",

        "Problem 3 - Highest Engaged User",

        "Problem 4 - Posting Hour Activity",

        "Problem 5 - Hashtag Relationships"
    ]
}


# =========================================
# CREATE DATASET DIRECTORY
# =========================================

os.makedirs(
    dataset_dir,
    exist_ok=True
)


# =========================================
# WRITE DATA.JSON
# =========================================

data_path = os.path.join(
    dataset_dir,
    "data.json"
)

with open(
    data_path,
    "w",
    encoding="utf-8"
) as f:

    json.dump(
        dashboard_data,
        f,
        indent=4
    )


# =========================================
# WRITE METADATA.JSON
# =========================================

metadata_path = os.path.join(
    dataset_dir,
    "metadata.json"
)

with open(
    metadata_path,
    "w",
    encoding="utf-8"
) as f:

    json.dump(
        metadata,
        f,
        indent=4
    )


# =========================================
# UPDATE DATASET REGISTRY
# =========================================

registry_path = os.path.join(
    datasets_dir,
    "index.json"
)


if os.path.isfile(registry_path):

    with open(
        registry_path,
        "r",
        encoding="utf-8"
    ) as f:

        registry = json.load(f)

else:

    registry = {
        "datasets": []
    }


registry["datasets"] = [

    dataset

    for dataset in registry["datasets"]

    if dataset["id"] != analysis_id
]


registry["datasets"].append({

    "id":
        analysis_id,

    "metadata":
        "metadata.json",

    "data":
        "data.json"
})


with open(
    registry_path,
    "w",
    encoding="utf-8"
) as f:

    json.dump(
        registry,
        f,
        indent=4
    )


# =========================================
# UPDATE LEGACY DATA COPY
# =========================================

legacy_data_dir = os.path.join(
    dashboard_dir,
    "data"
)

os.makedirs(
    legacy_data_dir,
    exist_ok=True
)

shutil.copy2(
    data_path,
    os.path.join(
        legacy_data_dir,
        "data.json"
    )
)


# =========================================
# FINAL OUTPUT
# =========================================

print()
print("==============================================")
print("       DASHBOARD DATA GENERATED")
print("==============================================")
print()

print("Dataset ID:")
print(" ", analysis_id)
print()

print("Dataset Name:")
print(" ", display_name)
print()

print("Posts:")
print(" ", total_posts)

print("Users:")
print(" ", len(users))

print("Hashtags:")
print(" ", len(hashtags))

print("Total Engagement:")
print(" ", total_engagement)

print()

print("Dashboard dataset:")
print(" ", dataset_dir)

print()

print("Registry updated:")
print(" ", registry_path)

print()
