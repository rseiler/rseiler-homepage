var name = 'rseiler';

module.exports = {

    dir: {
        target: 'target',
        build: 'target/build',
        compile: 'target/bin',
        vendor: 'bower_components',
        app: 'app',
        src: 'src',
        css: 'css/' + name,
        // JRebel requires that the files are put into the src directory - the files in the exploded directory will be ignored.
        //publicUiDir: '../webapp/src/main/webapp/WEB-INF/velocity/include',
        //publicUiResourcesDir: '../webapp/src/main/webapp/resources/ui-' + name
        publicUiDir: '../rseiler-webapp/exploded/WEB-INF/velocity/include',
        publicUiResourcesDir: '../rseiler-webapp/exploded/resources/ui-' + name
    },

    src: {
        lessMain: ['src/less/main.less'],
        less: ['src/less/**/*.less'],
        js: ['src/javascript/**/*.js'],
        ts: ['src/typescript/**/*.ts'],
        typings: ['src/typings/**/*.ts'],
        context: '/resources/ui-' + name
    },

    compile: {
        root: 'target/build/',
        tpl: ['target/build/**/*.tpl.html'],
        js: 'target/build/**/*.js'
    },

    velocity: {
        src: 'css-js-includes.vm',
        name: 'css-js-' + name + '-includes.vm'
    },

    meta: {
        module: name
    }
};