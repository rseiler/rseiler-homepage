/* jshint strict: false */
/* global require,process */

/**
 * Define dependencies
 */
var gulp = require('gulp'),
    gutil = require('gulp-util'),
    less = require('gulp-less'),
    path = require('path'),
    bowerFiles = require('gulp-bower-files'),
    uglify = require('gulp-uglify'),
    rename = require('gulp-rename'),
    clean = require('gulp-clean'),
    concat = require('gulp-concat'),
    inject = require("gulp-inject"),
    size = require('gulp-size'),
    merge = require('merge-stream'),
    foreach = require('gulp-foreach'),
    streamqueue = require('streamqueue'),
    ts = require('gulp-typescript'),
    tslint = require('gulp-tslint'),
    runSequence = require('run-sequence'),
    cliArgs = require('yargs').argv;

/**
 * Load config files
 */
var pkg = require('./package.json'),
    cfg = require('./build.config.js'),
    tsProject = ts.createProject({
        noExternalResolve: true
    }),
    bowerFilesConfig;

/**
 * CLI arguments
 */
pkg.version = (cliArgs.build !== undefined) ? cliArgs.build : pkg.version;


// ----------------------------------------------------------------------------
// DEFAULT TASKS
// ----------------------------------------------------------------------------

gulp.task('default', function () {
    gulp.start('compile');
});


// ----------------------------------------------------------------------------
// COMMON TASKS
// ----------------------------------------------------------------------------


/**
 * Clean build (bin) and compile (compile) directories
 */
gulp.task('clean', function () {
    return gulp.src([cfg.dir.target, cfg.dir.publicUiResourcesDir], {read: false})
        .pipe(clean({force: true}));
});

/**
 *  Watch for file changes
 */
gulp.task('watch', ['build'], function () {
    gulp.watch(cfg.src.js, ['js:watch:build']);
    gulp.watch(cfg.src.ts, ['ts:watch:build']);
    gulp.watch(cfg.src.less, ['less:watch:build']);
});


// ----------------------------------------------------------------------------
// BUILD TASKS
// ----------------------------------------------------------------------------

/**
 * BUILD
 */
gulp.task('build', function (callback) {
    runSequence(
        'clean',
        ['less:build', 'js:build', 'vendor:build'],
        'tomcat:clean',
        ['tomcat:copy:build', 'velocity:build'],
        callback);
});


/**
 * JAVASCRIPT
 * Build app related js files
 */
gulp.task('js:build', ['ts:lint', 'ts:build'], function () {
    // ignore files
    var src = [].concat(cfg.src.js).concat('!' + cfg.src.typings);
    return gulp.src(src)
        .pipe(gulp.dest(cfg.dir.build));

});


/**
 * JS:WATCH:BUILD
 */
gulp.task('js:watch:build', function (callback) {
    runSequence(
        'js:build',
        'velocity:build',
        'tomcat:copy:build',
        callback);
});

/**
 * JS:WATCH:BUILD
 */
gulp.task('ts:watch:build', function (callback) {
    runSequence(
        'ts:build',
        'velocity:build',
        callback);
});


/**
 * TYPESCRIPT
 * Lint and compile typescript app files
 */
gulp.task('ts:build', function () {
    var src = [].concat(cfg.src.ts).concat(cfg.src.typings);
    return gulp.src(src)
        .pipe(ts(tsProject)).js
        .pipe(gulp.dest(cfg.dir.build))
        .pipe(gulp.dest(cfg.dir.publicUiResourcesDir));
});

/**
 * TYPESCRIPT
 * Lint and compile typescript app files
 */
gulp.task('ts:lint', function () {
    var src = [].concat(cfg.src.ts).concat('!' + cfg.src.typings);
    return gulp.src(src)
        .pipe(tslint())
        .pipe(tslint.report('verbose', {emitError: false}));
});

/**
 * VENDOR
 * Copy vendor files
 */
gulp.task('vendor:build', function () {
    var destDir = path.join(cfg.dir.build, cfg.dir.vendor);
    return bowerFiles(bowerFilesConfig)
        .pipe(gulp.dest(destDir));
});

/**
 * LESS
 * Compile and autoprefix LESS files
 */
gulp.task('less:build', function () {
    return gulp.src(cfg.src.lessMain)
        .pipe(less({
            compress: true
        }).on('error', gutil.log))
        .pipe(rename({
            basename: pkg.name,
            suffix: "-" + pkg.version
        }))
        .pipe(gulp.dest(cfg.dir.build))
        .pipe(gulp.dest(cfg.dir.publicUiResourcesDir));
});

/**
 * LESS:WATCH:BUILD
 */
gulp.task('less:watch:build', function (callback) {
    runSequence(
        'less:build',
        'velocity:build',
        'tomcat:copy:build',
        callback);
});

/**
 * VELOCITY FRAGMENTS
 * inject css and js files
 */
gulp.task('velocity:build', function () {
    var src = {
        css: path.join(cfg.dir.build, '**', '*.css'),
        js: [
            path.join(cfg.dir.build, '**', '*.js'),
            path.join("!" + cfg.dir.build, cfg.dir.vendor, '**', '*')
        ]
    };

    var destDir = path.join(cfg.dir.publicUiDir);

    return merge(
        gulp.src([cfg.velocity.src])
            .pipe(inject(gulp.src(src.css, {read: false}), {
                addRootSlash: true,
                addPrefix: cfg.src.context,
                ignorePath: cfg.dir.build,
                starttag: '<!-- inject:css -->',
                transform: function (filepath) {
                    return '<link rel="stylesheet" href="' + filepath + '"/>';
                }
            }))
            .pipe(inject(bowerFiles(bowerFilesConfig), {
                addRootSlash: true,
                addPrefix: cfg.src.context,
                ignorePath: cfg.dir.build,
                starttag: '<!-- inject:jsvendor -->',
                transform: function (filepath) {
                    return '<script type="text/javascript" defer="defer" src="' + filepath + '"></script>';
                }
            }))
            .pipe(inject(gulp.src(src.js, {read: false}), {
                addRootSlash: true,
                addPrefix: cfg.src.context,
                ignorePath: cfg.dir.build,
                starttag: '<!-- inject:js -->',
                transform: function (filepath) {
                    return '<script type="text/javascript" defer="defer" src="' + filepath + '"></script>';
                }
            }))
            .pipe(rename(cfg.velocity.name))
            .pipe(gulp.dest(destDir))
//            .pipe(gulp.dest(cfg.dir.publicUiResourcesDir))
    );
});

/**
 * VELOCITY FRAGMENTS
 * inject css and js files
 */
gulp.task('velocity:compile', function () {
    var src = {
        js: path.join(cfg.dir.compile, '*.js')
    };

    var vmSrc = path.join(cfg.dir.publicUiDir, cfg.velocity.name);
    var destDir = path.join(cfg.dir.publicUiDir);

    return merge(
        gulp.src([vmSrc])
            .pipe(size())
            .pipe(foreach(function (stream, file) {
                return stream
                    .pipe(inject(gulp.src(src.js, {read: false}), {
                        addRootSlash: true,
                        addPrefix: cfg.src.context,
                        ignorePath: cfg.dir.compile,
                        starttag: '<!-- inject:min:js -->',
                        endtag: '<!-- endinject:min -->',
                        transform: function (filepath, file, i, length) {
                            return '<script type="text/javascript" defer="defer" src="' + filepath + '"></script>';
                        }
                    }))
                    .pipe(gulp.dest(destDir));
            }))
    );
});

/**
 * COPY TO TOMCAT
 */

gulp.task('tomcat:clean', function () {
    var vmCssFiles = path.join(cfg.dir.publicUiDir, "**", "css.*.vm");
    var vmJsFiles = path.join(cfg.dir.publicUiDir, "**", "js.*.vm");
    return gulp.src([cfg.dir.publicUiResourcesDir, vmCssFiles, vmJsFiles], {read: false})
        .pipe(clean({force: true}));
});

gulp.task('tomcat:copy:build', function () {
    var src = path.join(cfg.dir.build, '**', '*');
    var destDir = cfg.dir.publicUiResourcesDir;
    return gulp.src([src])
        .pipe(gulp.dest(destDir));
});


gulp.task('tomcat:copy:compile', function () {
    var src = path.join(cfg.dir.compile, '**', '*');
    var destDir = cfg.dir.publicUiResourcesDir;
    return gulp.src([src])
        .pipe(gulp.dest(destDir));
});


// ----------------------------------------------------------------------------
// COMPILE TASKS
// ----------------------------------------------------------------------------

/**
 * COMPILE
 */
gulp.task('compile', ['build'], function (callback) {
    runSequence(
        'js:compile',
        'velocity:compile',
        'tomcat:copy:compile',
        callback);
});

/**
 * JAVASCRIPT
 * Concat and minify app files in a single js file (<pkg.name>-<pkg.version>.js)
 */
gulp.task('js:compile', function () {
    var destDir = path.join(cfg.dir.compile);
    var filename = pkg.name + '.js';

    var srcDir = [
        cfg.compile.js,
        path.join('!' + cfg.compile.root, cfg.dir.vendor, '**', '*')
    ];

    return streamqueue({ objectMode: true },
        bowerFiles(bowerFilesConfig),
        gulp.src(srcDir))
        .pipe(concat(filename))
        .pipe(uglify())
        .pipe(rename({suffix: '-' + pkg.version}))
        .pipe(size({showFiles: true}))
        .pipe(gulp.dest(destDir));
});
