#!/bin/bash

# =========================================
# CHATTERWAVE AUTOMATED ANALYSIS
# =========================================

set -e

# -----------------------------------------
# CHECK INPUT
# -----------------------------------------

if [ -z "$1" ]; then
    echo "Usage: ./scripts/analyze.sh <dataset.log>"
    exit 1
fi

INPUT_FILE="$1"
DATASET_NAME=$(basename "$INPUT_FILE" .log)

echo
echo "=============================================="
echo "        CHATTERWAVE ANALYSIS PIPELINE"
echo "=============================================="
echo
echo "Dataset : $INPUT_FILE"
echo


# -----------------------------------------
# CHECK FILE
# -----------------------------------------

if [ ! -f "$INPUT_FILE" ]; then
    echo "ERROR: Dataset file not found."
    exit 1
fi


# -----------------------------------------
# CREATE UNIQUE ANALYSIS ID
# -----------------------------------------

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

ANALYSIS_ID="${DATASET_NAME}_${TIMESTAMP}"

HDFS_BASE="/chatterwave/analysis/${ANALYSIS_ID}"

LOCAL_BASE="$HOME/ChatterWave/analysis_runs/${ANALYSIS_ID}"


mkdir -p "$LOCAL_BASE"


echo "Analysis ID : $ANALYSIS_ID"
echo


# -----------------------------------------
# UPLOAD INPUT TO HDFS
# -----------------------------------------

echo "[1/6] Preparing HDFS input..."

hdfs dfs -mkdir -p "$HDFS_BASE/input"

hdfs dfs -put -f "$INPUT_FILE" \
    "$HDFS_BASE/input/"

echo "      Input uploaded."
echo


# -----------------------------------------
# PROBLEM 1
# -----------------------------------------

echo "[2/6] Running Problem 1..."

hadoop jar chatterwave.jar \
    problem1.HashtagDriver \
    "$HDFS_BASE/input" \
    "$HDFS_BASE/output/problem1"

echo "      Problem 1 completed."
echo


# -----------------------------------------
# PROBLEM 2
# -----------------------------------------

echo "[3/6] Running Problem 2..."

hadoop jar chatterwave.jar \
    problem2.EngagementDriver \
    "$HDFS_BASE/input" \
    "$HDFS_BASE/output/problem2"

echo "      Problem 2 completed."
echo

# -----------------------------------------
# PROBLEM 3
# -----------------------------------------

echo "[4/6] Running Problem 3..."

hadoop jar chatterwave.jar \
    problem3.EngagementDriver \
    "$HDFS_BASE/input" \
    "$HDFS_BASE/output/problem3-stage1" \
    "$HDFS_BASE/output/problem3"

echo "      Problem 3 completed."
echo

# -----------------------------------------
# PROBLEM 4
# -----------------------------------------

echo "[5/6] Running Problem 4..."

hadoop jar chatterwave.jar \
    problem4.PostingHourDriver \
    "$HDFS_BASE/input" \
    "$HDFS_BASE/output/problem4"

echo "      Problem 4 completed."
echo


# -----------------------------------------
# PROBLEM 5
# -----------------------------------------

echo "[6/6] Running Problem 5..."

hadoop jar chatterwave.jar \
    problem5.HashtagPairDriver \
    "$HDFS_BASE/input" \
    "$HDFS_BASE/output/problem5"

echo "      Problem 5 completed."
echo


# -----------------------------------------
# COPY RESULTS LOCALLY
# -----------------------------------------

echo "Collecting analysis results..."

for problem in problem1 problem2 problem3 problem4 problem5
do

    mkdir -p "$LOCAL_BASE/$problem"

    hdfs dfs -get \
        "$HDFS_BASE/output/$problem/part-r-00000" \
        "$LOCAL_BASE/$problem/"

done


echo
echo "=============================================="
echo "       CHATTERWAVE ANALYSIS COMPLETE"
echo "=============================================="
echo
echo "Dataset:"
echo "  $INPUT_FILE"
echo
echo "Analysis ID:"
echo "  $ANALYSIS_ID"
echo
echo "HDFS results:"
echo "  $HDFS_BASE/output/"
echo
echo "Local results:"
echo "  $LOCAL_BASE/"
echo

# -----------------------------------------
# DASHBOARD CREATION
# -----------------------------------------

echo
read -r -p "Create dashboard for this analysis? [y/n]: " CREATE_DASHBOARD

if [[ "$CREATE_DASHBOARD" =~ ^[Yy]$ ]]; then

    echo
    read -r -p "Enter dataset name for the dashboard: " DISPLAY_NAME

    if [ -z "$DISPLAY_NAME" ]; then

        echo
        echo "ERROR: Dataset name cannot be empty."
        exit 1

    fi

    echo
    echo "Generating dashboard data..."

    python3 scripts/generate_dataset.py \
        "$INPUT_FILE" \
        "$ANALYSIS_ID" \
        "$DISPLAY_NAME"

    echo
    echo "Dashboard dataset generated successfully."
    echo

    read -r -p "Open ChatterWave Analysis Portal? [y/n]: " OPEN_PORTAL

    if [[ "$OPEN_PORTAL" =~ ^[Yy]$ ]]; then

        echo
        echo "Opening ChatterWave Analysis Portal..."

        if command -v explorer.exe >/dev/null 2>&1; then

            explorer.exe "http://localhost:8000"

        elif command -v xdg-open >/dev/null 2>&1; then

            xdg-open "http://localhost:8000"

        else

            echo
            echo "Portal is available at:"
            echo "  http://localhost:8000"

        fi

    else

        echo
        echo "Portal available at:"
        echo "  http://localhost:8000"

    fi

else

    echo
    echo "Dashboard creation skipped."
    echo "Analysis results remain available at:"
    echo "  $LOCAL_BASE/"

fi

echo
echo "=============================================="
echo "             PIPELINE FINISHED"
echo "=============================================="
echo
