import { RouteRecordRaw } from "vue-router";

export const RouteKatalogFaecher : RouteRecordRaw = {
	name: "faecher",
	path: "/kataloge/faecher/:id?/:slug([a-zA-Z-0-9:]+)?",
	components: {
		default: () => import("~/components/faecher/SFaecherApp.vue"),
		liste: () => import("~/components/faecher/SFaecherAuswahl.vue")
	}
}
