let whoSangThis: string = '';

const singles = [
    { song: 'touch of grey', artist: 'grateful dead' },
    { song: 'paint it black', artist: 'rolling stones' },
];

const single = singles.find((s) => s.song === whoSangThis);

// console.log(single.artist);

if (single) {
    console.log(single.artist);
}