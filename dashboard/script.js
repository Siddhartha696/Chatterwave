// =========================================
// CHATTERWAVE DATASET PORTAL
// =========================================

let currentDataset = null;
let dashboardData = null;


// =========================================
// DATASET SELECTOR
// =========================================

async function loadDatasetSelector() {

    const datasetList =
        document.getElementById("datasetList");

    try {

        const registryResponse =
            await fetch("datasets/index.json");

        if (!registryResponse.ok) {
            throw new Error(
                "Could not load dataset registry."
            );
        }

        const registry =
            await registryResponse.json();

        datasetList.innerHTML = "";

        for (const dataset of registry.datasets) {

            try {

                const metadataResponse =
                    await fetch(
                        `datasets/${dataset.id}/${dataset.metadata}`
                    );

                if (!metadataResponse.ok) {
                    throw new Error(
                        "Could not load dataset metadata."
                    );
                }

                const metadata =
                    await metadataResponse.json();

                const card =
                    document.createElement("div");

                card.className =
                    "dataset-card";

                card.innerHTML = `
                    <h3>
                        ${metadata.name}
                    </h3>

                    <p>
                        ${metadata.description}
                    </p>

                    <div class="dataset-meta">

                        <div class="dataset-meta-item">
                            <strong>
                                ${metadata.posts}
                            </strong>
                            <span>POSTS</span>
                        </div>

                        <div class="dataset-meta-item">
                            <strong>
                                ${metadata.users}
                            </strong>
                            <span>USERS</span>
                        </div>

                        <div class="dataset-meta-item">
                            <strong>
                                ${metadata.hashtags}
                            </strong>
                            <span>HASHTAGS</span>
                        </div>

                    </div>

                    <div class="dataset-open">
                        VIEW ANALYSIS →
                    </div>
                `;

                card.addEventListener(
                    "click",
                    () => selectDataset(dataset)
                );

                datasetList.appendChild(card);

            } catch (error) {

                console.error(
                    `Failed to load dataset: ${dataset.id}`,
                    error
                );

            }
        }

        if (registry.datasets.length === 0) {

            datasetList.innerHTML = `
                <div class="dataset-loading">
                    No saved analyses found.
                </div>
            `;
        }

    } catch (error) {

        console.error(
            "Failed to load datasets:",
            error
        );

        datasetList.innerHTML = `
            <div class="dataset-loading">
                Unable to load saved analyses.
            </div>
        `;
    }
}


// =========================================
// SELECT DATASET
// =========================================

async function selectDataset(dataset) {

    try {

        const response =
            await fetch(
                `datasets/${dataset.id}/${dataset.data}`
            );

        if (!response.ok) {
            throw new Error(
                "Could not load selected dataset."
            );
        }

        currentDataset =
            await response.json();

        dashboardData =
            currentDataset;

        window.dashboardData =
            currentDataset;

        // Hide dataset selector
        const selector =
            document.getElementById(
                "datasetSelector"
            );

        selector.style.display = "none";

        // Render dashboard
        renderDashboard();

    } catch (error) {

        console.error(
            "Failed to load dataset:",
            error
        );

        alert(
            "Unable to load this dataset."
        );
    }
}


// =========================================
// MAIN RENDER
// =========================================

function renderDashboard() {

    renderOverview();

    renderHashtagChart();

    renderEngagementChart();

    renderTopUser();

    renderActivityChart();

    renderRelationships();

    document.getElementById(
        "lastUpdated"
    ).textContent =
        "Updated " +
        new Date().toLocaleTimeString();
}


// =========================================
// OVERVIEW
// =========================================

function renderOverview() {

    const overview =
        dashboardData.overview;

    document.getElementById("totalPosts")
        .textContent =
        overview.total_posts;

    document.getElementById("totalUsers")
        .textContent =
        overview.total_users;

    document.getElementById("totalHashtags")
        .textContent =
        overview.total_hashtags;

    document.getElementById("totalEngagement")
        .textContent =
        overview.total_engagement
            .toLocaleString();
}


// =========================================
// CHART DEFAULTS
// =========================================

const chartText = "#929292";
const chartGrid = "#292929";
const chartGold = "#e6393f";
const chartGoldSoft = "#a92b30";


const commonOptions = {

    responsive: true,

    maintainAspectRatio: false,

    plugins: {

        legend: {
            display: false
        },

        tooltip: {

            backgroundColor: "#151512",

            borderColor: "#3a382d",

            borderWidth: 1,

            titleColor: "#e8e4d8",

            bodyColor: "#b7a46a",

            padding: 10
        }
    },

    scales: {

        x: {

            ticks: {

                color: chartText,

                font: {
                    size: 9
                }
            },

            grid: {
                color: chartGrid
            }
        },

        y: {

            beginAtZero: true,

            ticks: {

                color: chartText,

                font: {
                    size: 9
                }
            },

            grid: {
                color: chartGrid
            }
        }
    }
};


// =========================================
// PROBLEM 1
// HASHTAG FREQUENCY
// =========================================

function renderHashtagChart() {

    const data =
        dashboardData.problem1;

    const sorted =
        [...data]
            .sort(
                (a, b) =>
                    b.count - a.count
            );

    new Chart(

        document.getElementById(
            "hashtagChart"
        ),

        {

            type: "bar",

            data: {

                labels:
                    sorted.map(
                        item => item.hashtag
                    ),

                datasets: [

                    {

                        data:
                            sorted.map(
                                item => item.count
                            ),

                        backgroundColor:
                            chartGold,

                        borderWidth: 0,

                        borderRadius: 2
                    }

                ]
            },

            options: {

                ...commonOptions,

                indexAxis: "y",

                scales: {

                    x: {

                        beginAtZero: true,

                        ticks: {

                            color: chartText,

                            font: {
                                size: 9
                            }
                        },

                        grid: {
                            color: chartGrid
                        }
                    },

                    y: {

                        ticks: {

                            color: chartText,

                            font: {
                                size: 9
                            }
                        },

                        grid: {
                            display: false
                        }
                    }
                }
            }
        }
    );
}


// =========================================
// PROBLEM 2
// AVERAGE ENGAGEMENT
// =========================================

function renderEngagementChart() {

    const data =
        dashboardData.problem2;

    const sorted =
        [...data]
            .sort(
                (a, b) =>
                    b.average - a.average
            );

    new Chart(

        document.getElementById(
            "engagementChart"
        ),

        {

            type: "bar",

            data: {

                labels:
                    sorted.map(
                        item => item.hashtag
                    ),

                datasets: [

                    {

                        data:
                            sorted.map(
                                item => item.average
                            ),

                        backgroundColor:
                            chartGoldSoft,

                        borderWidth: 0,

                        borderRadius: 2
                    }

                ]
            },

            options: {

                ...commonOptions,

                indexAxis: "y",

                scales: {

                    x: {

                        beginAtZero: true,

                        ticks: {

                            color: chartText,

                            font: {
                                size: 9
                            }
                        },

                        grid: {
                            color: chartGrid
                        }
                    },

                    y: {

                        ticks: {

                            color: chartText,

                            font: {
                                size: 9
                            }
                        },

                        grid: {
                            display: false
                        }
                    }
                }
            }
        }
    );
}


// =========================================
// PROBLEM 3
// HIGHEST ENGAGED USER
// =========================================

function renderTopUser() {

    const data =
        dashboardData.problem3;

    if (!data) {
        return;
    }

    document.getElementById(
        "topUser"
    ).textContent =
        data.user;

    document.getElementById(
        "userInitial"
    ).textContent =
        data.user
            .charAt(0)
            .toUpperCase();

    document.getElementById(
        "topUserEngagement"
    ).textContent =
        data.engagement
            .toLocaleString();
}


// =========================================
// PROBLEM 4
// POSTING ACTIVITY
// =========================================

function renderActivityChart() {

    const data =
        dashboardData.problem4;

    new Chart(

        document.getElementById(
            "activityChart"
        ),

        {

            type: "bar",

            data: {

                labels:
                    data.map(
                        item =>
                            item.hour + ":00"
                    ),

                datasets: [

                    {

                        data:
                            data.map(
                                item => item.count
                            ),

                        backgroundColor:
                            chartGold,

                        borderWidth: 0,

                        borderRadius: 2
                    }

                ]
            },

            options:
                commonOptions
        }
    );
}


// =========================================
// PROBLEM 5
// HASHTAG RELATIONSHIPS
// =========================================

let relationshipData = [];


function renderRelationships() {

    relationshipData =
        [...dashboardData.problem5];

    updateRelationshipTable();

    const search =
        document.getElementById(
            "relationshipSearch"
        );

    const sort =
        document.getElementById(
            "relationshipSort"
        );

    search.addEventListener(
        "input",
        updateRelationshipTable
    );

    sort.addEventListener(
        "change",
        updateRelationshipTable
    );
}


function updateRelationshipTable() {

    const search =
        document.getElementById(
            "relationshipSearch"
        )
        .value
        .toLowerCase()
        .trim();

    const sort =
        document.getElementById(
            "relationshipSort"
        )
        .value;


    let filtered =
        relationshipData.filter(
            item => {

                return (

                    item.hashtag1
                        .toLowerCase()
                        .includes(search)

                    ||

                    item.hashtag2
                        .toLowerCase()
                        .includes(search)

                );
            }
        );


    if (sort === "count") {

        filtered.sort(
            (a, b) =>
                b.count - a.count
        );

    } else {

        filtered.sort(
            (a, b) => {

                const first =
                    a.hashtag1 +
                    a.hashtag2;

                const second =
                    b.hashtag1 +
                    b.hashtag2;

                return first.localeCompare(
                    second
                );
            }
        );
    }


    const table =
        document.getElementById(
            "relationshipTable"
        );

    table.innerHTML = "";


    filtered.forEach(
        item => {

            const row =
                document.createElement(
                    "tr"
                );

            row.innerHTML = `

                <td>
                    ${item.hashtag1}
                </td>

                <td>
                    ${item.hashtag2}
                </td>

                <td>
                    ${item.count}
                </td>

            `;

            table.appendChild(row);
        }
    );
}


// =========================================
// NAVIGATION ACTIVE STATE
// =========================================

const navItems =
    document.querySelectorAll(
        ".nav-item"
    );


window.addEventListener(
    "scroll",
    () => {

        let current = "";

        document
            .querySelectorAll(".section")
            .forEach(
                section => {

                    const top =
                        section.offsetTop -
                        180;

                    if (
                        window.scrollY >= top
                    ) {

                        current =
                            section.id;
                    }
                }
            );


        navItems.forEach(
            item => {

                item.classList.remove(
                    "active"
                );

                if (
                    item.getAttribute(
                        "href"
                    )
                    === "#" + current
                ) {

                    item.classList.add(
                        "active"
                    );
                }
            }
        );
    }
);


// =========================================
// START APPLICATION
// =========================================

// Start with the dataset selector.
// The dashboard is rendered only after
// the user chooses a saved dataset.

loadDatasetSelector();
