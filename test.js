// To run this script, invoke
//    node test.js [optional username]
// if provided, script will be invoked for username. Otherwise, user testuser
// will be used every time


async function makeRequest(method, path, body) {
    return await fetch(`http://localhost:8080${path}`, {
        method,
        body: body ? JSON.stringify(body) : undefined,
        headers: {
            'X-User-Name': process.argv[2] || 'testuser',
            'Content-Type': body ? 'application/json' : undefined
        }
    })
}

const formatDate = (daysAgo) => {
    const date = new Date();
    date.setDate(date.getDate() - daysAgo);
    return date.toISOString().split('T')[0];
};

const padZero = (num) => String(num).padStart(2, '0');

async function run() {
    console.log("Generating data");

    const qualities = ["BAD", "GOOD", "OK"];

    for (let i = 1; i <= 40; i++) {
        const targetDate = formatDate(i);

        const payload = {
            date: targetDate,
            // pseudo-random
            timeStart: `${padZero(18 + (i % 6))}:${i % 2 === 0 ? '30' : '00'}:00`,
            timeEnd: `${padZero(6 + (i % 5))}:${i % 3 === 0 ? '15' : '45'}:00`,
            quality: qualities[Math.floor(Math.random() * qualities.length)]
        };
        console.log(payload)
        const resp = await makeRequest('POST', `/api/sleep/log`, payload)
        if (resp.status !== 201) console.error(await resp.json())
        console.log('sent log ' + i)
    }

    console.log("\nData generation complete. Fetching logs and reports\n");

    console.log(await (await makeRequest('GET', `/api/sleep/log`)).json())

    console.log("\n");

    console.log(await (await makeRequest('GET', '/api/sleep/report')).json())

    console.log("\nDone!");
}

run();