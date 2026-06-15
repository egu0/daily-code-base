"use strict";
let whoSangThis = '';
const singles = [
    { song: 'touch of grey', artist: 'grateful dead' },
    { song: 'paint it black', artist: 'rolling stones' },
];
const single = singles.find((s) => s.song === whoSangThis);
if (single) {
    console.log(single.artist);
}
//# sourceMappingURL=04_strict_null_checks.js.map